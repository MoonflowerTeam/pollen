package gg.moonflower.pollen.api.registry.resource;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

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
    public static void registerReloadListener(PackType type, PollinatedPreparableReloadListener listener) {
        Platform.error();
    }

    /**
     * Registers a built-in resource pack.
     *
     * <p>A built-in resource pack is an extra resource pack provided by your mod similar to the "Programmer Art" resource pack.
     *
     * <p>The resource pack is located is in the mod JAR file under the {@code "resourcepacks/<id path>"} directory. {@code id path} being the path specified
     * in the id of this built-in resource pack.
     *
     * @param id               The location to place the resource pack at. It will also be used for the name of the resource pack
     * @param container        The pollinated mod container instance. This can be retrieved using {@link PollinatedModContainer#get(String)}
     * @param enabledByDefault Whether to automatically enable the resource pack by default
     * @return Whether the resource pack was successfully registered
     */
    @ExpectPlatform
    public static boolean registerBuiltinResourcePack(ResourceLocation id, PollinatedModContainer container, boolean enabledByDefault) {
        return Platform.error();
    }
}
