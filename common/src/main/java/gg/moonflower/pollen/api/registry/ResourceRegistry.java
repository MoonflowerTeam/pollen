package gg.moonflower.pollen.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

/**
 * A wrapper for the Fabric resource API that also functions on Forge.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class ResourceRegistry {

    private ResourceRegistry() {
    }

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

    @ExpectPlatform
    public static boolean registerBuiltinResourcePack(ResourceLocation id, PollinatedModContainer container, boolean enabledByDefault) {
        return Platform.error();
    }
}
