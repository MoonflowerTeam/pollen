package gg.moonflower.pollen.api.config.fabric;

import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.platform.Platform;
import org.jetbrains.annotations.ApiStatus;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

@ApiStatus.Internal
public class ConfigManagerImpl {

    private static final Map<String, Map<PollinatedConfigType, ModConfig>> CONFIGS = new HashMap<>();

    public static <T> T register(String modId, PollinatedConfigType type, Function<PollinatedConfigBuilder, T> consumer) {
        return register(modId, type, defaultConfigName(type, modId), consumer);
    }

    public static <T> T register(String modId, PollinatedConfigType type, String fileName, Function<PollinatedConfigBuilder, T> consumer) {
        Map<PollinatedConfigType, ModConfig> map = CONFIGS.computeIfAbsent(modId, key -> new EnumMap<>(PollinatedConfigType.class));
        return Platform.error();
    }

    private static String defaultConfigName(PollinatedConfigType type, String modId) {
        return String.format("%s-%s.toml", modId, type.name().toLowerCase(Locale.ROOT));
    }
}
