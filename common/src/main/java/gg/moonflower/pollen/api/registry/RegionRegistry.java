package gg.moonflower.pollen.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.levelgen.biome.PollinatedRegion;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.resources.ResourceLocation;

/**
 * @author ebo2022
 * @since 1.5.0
 */
public final class RegionRegistry {

    private RegionRegistry() {
    }

    /**
     * Registers a region to the generation.
     *
     * @param modId  The mod to register the region for
     * @param region The region to register
     */
    @ExpectPlatform
    public static void register(String modId, PollinatedRegion region) {
        Platform.error();
    }

    /**
     * Registers a region to the generation with a custom name.
     *
     * @param name   The custom name to register the region with
     * @param region The region to register
     */
    @ExpectPlatform
    public static void register(ResourceLocation name, PollinatedRegion region) {
        Platform.error();
    }
}
