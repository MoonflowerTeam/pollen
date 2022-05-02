package gg.moonflower.pollen.api.config;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * Registers all configs. These should be called statically in the mod class.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class ConfigManager {

    private ConfigManager() {
    }

    /**
     * Registers a new config for the specified mod for the specified type and uses the default <code>modid-type.toml</code> filename.
     *
     * @param modId    The id of the mod to register config for
     * @param type     The type of config to register
     * @param consumer A constructor for a config object
     * @param <T>      The type of object to return
     * @return The configured config object
     */
    public static <T> T register(String modId, PollinatedConfigType type, Function<PollinatedConfigBuilder, T> consumer) {
        return register(modId, type, String.format("%s-%s.toml", modId, type.name().toLowerCase(Locale.ROOT)), consumer);
    }

    /**
     * Registers a new config for the specified mod for the specified type.
     *
     * @param modId    The id of the mod to register config for
     * @param type     The type of config to register
     * @param fileName The name of the actual config file
     * @param consumer A constructor for a config object
     * @param <T>      The type of object to return
     * @return The configured config object
     */
    @ExpectPlatform
    public static <T> T register(String modId, PollinatedConfigType type, String fileName, Function<PollinatedConfigBuilder, T> consumer) {
        return Platform.error();
    }

    /**
     * Retrieves a mod config by the specified mod id and type.
     *
     * @param modId The id of the mod to get config for
     * @param type  The type of config to get
     * @return The config retrieved or nothing if not registered
     */
    @ExpectPlatform
    public static Optional<PollinatedModConfig> get(String modId, PollinatedConfigType type) {
        return Platform.error();
    }
}
