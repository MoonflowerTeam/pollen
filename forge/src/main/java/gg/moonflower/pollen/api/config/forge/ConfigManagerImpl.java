package gg.moonflower.pollen.api.config.forge;

import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.PollinatedModConfig;
import gg.moonflower.pollen.api.event.events.ConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@ApiStatus.Internal
public class ConfigManagerImpl {

    private static final Map<String, Map<PollinatedConfigType, PollinatedModConfig>> CONFIGS = new ConcurrentHashMap<>();
    private static final Set<String> registeredEvents = ConcurrentHashMap.newKeySet();

    public static <T> T register(String modId, PollinatedConfigType type, String fileName, Function<PollinatedConfigBuilder, T> consumer) {
        ModLoadingContext context = ModLoadingContext.get();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();


        Pair<T, ForgeConfigSpec> pair = new PollinatedConfigBuilderImpl(new ForgeConfigSpec.Builder()).configure(consumer);
        ModConfig config = new ModConfig(convert(type), pair.getRight(), context.getActiveContainer(), fileName);
        context.getActiveContainer().addConfig(config);

        if (registeredEvents.add(modId)) {
            bus.<ModConfigEvent.Loading>addListener(event -> {
                ModConfig modConfig = event.getConfig();
                get(modConfig.getModId(), convert(modConfig.getType())).ifPresent(c -> {
                    ConfigEvent.LOADING.invoker().configChanged(c);
                });
            });
            bus.<ModConfigEvent.Reloading>addListener(event -> {
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
        return switch (type) {
            case COMMON -> ModConfig.Type.COMMON;
            case CLIENT -> ModConfig.Type.CLIENT;
            case SERVER -> ModConfig.Type.SERVER;
        };
    }

    public static PollinatedConfigType convert(ModConfig.Type type) {
        return switch (type) {
            case COMMON -> PollinatedConfigType.COMMON;
            case CLIENT -> PollinatedConfigType.CLIENT;
            case SERVER -> PollinatedConfigType.SERVER;
        };
    }

    public static Optional<PollinatedModConfig> get(String modId, PollinatedConfigType type) {
        return !CONFIGS.containsKey(modId) ? Optional.empty() : Optional.ofNullable(CONFIGS.get(modId).get(type));
    }
}
