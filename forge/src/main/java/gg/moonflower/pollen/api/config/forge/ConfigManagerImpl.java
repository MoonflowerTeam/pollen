package gg.moonflower.pollen.api.config.forge;

import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.PollinatedModConfig;
import gg.moonflower.pollen.api.event.events.ConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Function;

@ApiStatus.Internal
public class ConfigManagerImpl {

    private static final Map<String, Map<PollinatedConfigType, PollinatedModConfig>> CONFIGS = new HashMap<>();
    private static final Set<String> registeredEvents = new HashSet<>();

    public static <T> T register(String modId, PollinatedConfigType type, String fileName, Function<PollinatedConfigBuilder, T> consumer) {
        ModLoadingContext context = ModLoadingContext.get();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        Pair<T, ForgeConfigSpec> pair = new PollinatedConfigBuilderImpl(new ForgeConfigSpec.Builder()).configure(consumer);
        ModConfig config = new ModConfig(convert(type), pair.getRight(), context.getActiveContainer(), fileName);
        context.getActiveContainer().addConfig(config);

        if (registeredEvents.add(modId)) {
            bus.<ModConfig.Loading>addListener(event -> {
                ModConfig modConfig = event.getConfig();
                get(modConfig.getModId(), convert(modConfig.getType())).ifPresent(c -> {
                    ConfigEvent.LOADING.invoker().configChanged(c);
                });
            });
            bus.<ModConfig.Reloading>addListener(event -> {
                ModConfig modConfig = event.getConfig();
                get(modConfig.getModId(), convert(modConfig.getType())).ifPresent(c -> {
                    ConfigEvent.RELOADING.invoker().configChanged(c);
                });
            });
        }

        CONFIGS.computeIfAbsent(modId, __ -> new EnumMap<>(PollinatedConfigType.class)).put(type, new PollinatedModConfigImpl(config));
        return pair.getLeft();
    }

    public static ModConfig.Type convert(PollinatedConfigType type) {
        switch (type) {
            case COMMON:
                return ModConfig.Type.COMMON;
            case CLIENT:
                return ModConfig.Type.CLIENT;
            case SERVER:
                return ModConfig.Type.SERVER;
            default:
                throw new UnsupportedOperationException("Unknown config type: " + type);
        }
    }

    public static PollinatedConfigType convert(ModConfig.Type type) {
        switch (type) {
            case COMMON:
                return PollinatedConfigType.COMMON;
            case CLIENT:
                return PollinatedConfigType.CLIENT;
            case SERVER:
                return PollinatedConfigType.SERVER;
            default:
                throw new UnsupportedOperationException("Unknown config type: " + type);
        }
    }

    public static Optional<PollinatedModConfig> get(String modId, PollinatedConfigType type) {
        return !CONFIGS.containsKey(modId) ? Optional.empty() : Optional.ofNullable(CONFIGS.get(modId).get(type));
    }
}
