package gg.moonflower.pollen.impl.config;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.config.v1.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.config.v1.PollinatedConfigType;
import gg.moonflower.pollen.api.config.v1.PollinatedModConfig;

import java.util.Optional;
import java.util.function.Function;

public class ConfigManagerImpl {
    @ExpectPlatform
    public static <T> T register(String modId, PollinatedConfigType type, String fileName, Function<PollinatedConfigBuilder, T> consumer) {
        throw new AssertionError("Expected platform method");
    }

    @ExpectPlatform
    public static Optional<PollinatedModConfig> get(String modId, PollinatedConfigType type) {
        throw new AssertionError("Expected platform method");
    }
}
