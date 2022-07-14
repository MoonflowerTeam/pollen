package gg.moonflower.pollen.api.config.fabric;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.event.events.ConfigEvent;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketContext;
import gg.moonflower.pollen.core.network.fabric.ClientboundSyncConfigDataPacket;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ConfigTracker {

    public static final ConfigTracker INSTANCE = new ConfigTracker();
    private static final Logger LOGGER = LogManager.getLogger();
    private final ConcurrentHashMap<String, PollinatedModConfigImpl> fileMap;
    private final EnumMap<PollinatedConfigType, Set<PollinatedModConfigImpl>> configSets;
    private final ConcurrentHashMap<String, Map<PollinatedConfigType, PollinatedModConfigImpl>> configsByMod;

    private ConfigTracker() {
        this.fileMap = new ConcurrentHashMap<>();
        this.configSets = new EnumMap<>(PollinatedConfigType.class);
        this.configsByMod = new ConcurrentHashMap<>();
        for (PollinatedConfigType type : PollinatedConfigType.values())
            this.configSets.put(type, Collections.synchronizedSet(new LinkedHashSet<>()));
    }

    void trackConfig(PollinatedModConfigImpl config) {
        if (this.fileMap.containsKey(config.getFileName())) {
            LOGGER.error("Detected config file conflict {} between {} and {}", config.getFileName(), this.fileMap.get(config.getFileName()).getModId(), config.getModId());
            throw new RuntimeException("Config conflict detected!");
        }
        this.fileMap.put(config.getFileName(), config);
        this.configSets.get(config.getType()).add(config);
        this.configsByMod.computeIfAbsent(config.getModId(), (k) -> new EnumMap<>(PollinatedConfigType.class)).put(config.getType(), config);
        LOGGER.debug("Config file {} for {} tracking", config.getFileName(), config.getModId());
    }

    public void loadConfigs(PollinatedConfigType type, Path configBasePath) {
        LOGGER.debug("Loading configs type {}", type);
        this.configSets.get(type).forEach(config -> openConfig(config, configBasePath));
    }

    public void unloadConfigs(PollinatedConfigType type, Path configBasePath) {
        LOGGER.debug("Unloading configs type {}", type);
        this.configSets.get(type).forEach(config -> closeConfig(config, configBasePath));
    }

    public List<Pair<String, ClientboundSyncConfigDataPacket>> syncConfigs(boolean isLocal) {
        return this.configSets.get(PollinatedConfigType.SERVER).stream().map(mc -> {
            try {
                return Pair.of("Config " + mc.getFileName(), new ClientboundSyncConfigDataPacket(mc.getFileName(), Files.readAllBytes(mc.getFullPath())));
            } catch (IOException e) {
                LOGGER.error("Failed to sync {} config for {}", mc.getType(), mc.getModId(), e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private void openConfig(PollinatedModConfigImpl config, Path configBasePath) {
        CommentedFileConfig configData = config.getHandler().reader(configBasePath).apply(config);
        config.setConfigData(configData);
        ConfigEvent.LOADING.invoker().configChanged(config);
        config.save();
    }

    private void closeConfig(PollinatedModConfigImpl config, Path configBasePath) {
        if (config.getConfigData() != null) {
            config.save();
            config.getHandler().unload(configBasePath, config);
            config.setConfigData(null);
        }
    }

    public void receiveSyncedConfig(ClientboundSyncConfigDataPacket pkt, PollinatedPacketContext ctx) {
        if (!Minecraft.getInstance().isLocalServer() && this.fileMap.containsKey(pkt.getFileName())) {
            PollinatedModConfigImpl config = this.fileMap.get(pkt.getFileName());
            config.setConfigData(TomlFormat.instance().createParser().parse(new ByteArrayInputStream(pkt.getFileData())));
            ConfigEvent.RELOADING.invoker().configChanged(config);
        }
    }

    public void loadDefaultServerConfigs() {
        this.configSets.get(PollinatedConfigType.SERVER).forEach(config -> {
            CommentedConfig commentedConfig = CommentedConfig.inMemory();
            config.getSpec().correct(commentedConfig);
            config.setConfigData(commentedConfig);
            ConfigEvent.LOADING.invoker().configChanged(config);
        });
    }

    @Nullable
    public String getConfigFileName(String modId, PollinatedConfigType type) {
        return Optional.ofNullable(this.configsByMod.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null)).flatMap(config -> Optional.ofNullable(config.getFullPath())).map(Object::toString).orElse(null);
    }

    public Optional<PollinatedModConfigImpl> getConfig(String modId, PollinatedConfigType type) {
        return Optional.ofNullable(this.configsByMod.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null));
    }
}
