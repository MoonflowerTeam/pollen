package gg.moonflower.pollen.api.resources;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

/**
 * A wrapper for the Fabric resource API that also functions on Forge.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class ResourceRegistry {

    /**
     * Registers a resource reload listener for the specified pack type.
     *
     * @param type     The type of resources to register for
     * @param listener The resource listener to add
     */
    @ExpectPlatform
    public static void registerReloadListener(PackType type, PreparableReloadListener listener) {
        Platform.error();
    }
}
