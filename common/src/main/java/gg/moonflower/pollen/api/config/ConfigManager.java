package gg.moonflower.pollen.api.config;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;

import java.util.function.Function;

public class ConfigManager {

    @ExpectPlatform
    public static <T> T register(String modId, PollinatedConfigType type, Function<PollinatedConfigBuilder, T> consumer) {
        return Platform.error();
    }

    @ExpectPlatform
    public static <T> T register(String modId, PollinatedConfigType type, String fileName, Function<PollinatedConfigBuilder, T> consumer) {
        return Platform.error();
    }
}
