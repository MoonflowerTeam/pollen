package gg.moonflower.pollen.api.util;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Wraps each mod loader's mod files in a common implementation for querying basic mod info.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedModContainer {

    /**
     * Retrieves a mod container from the specified mod id.
     *
     * @param modId The id of the mdd to fetch
     * @return A container wrapping each mod loader
     */
    @ExpectPlatform
    static Optional<PollinatedModContainer> get(String modId) {
        return Platform.error();
    }

    /**
     * @return The brand of this container
     */
    String getBrand();

    /**
     * Resolves a path inside the mod.
     *
     * @param path The path of the file inside the jar
     * @return A path inside the jar file
     */
    Path resolve(String path);

    /**
     * @return The id of the mod
     */
    String getId();

    /**
     * @return The name of the mod
     */
    String getName();

    /**
     * @return The readable version of the mod
     */
    String getVersion();

    /**
     * @return The visible display name of the mod
     */
    default String getDisplayName() {
        if (this.getName() != null) {
            return this.getName();
        } else {
            return this.getBrand() + " Mod \"" + this.getId() + "\"";
        }
    }
}
