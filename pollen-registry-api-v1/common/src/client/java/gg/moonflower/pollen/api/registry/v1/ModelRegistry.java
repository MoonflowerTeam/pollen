package gg.moonflower.pollen.api.registry.v1;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.function.Consumer;

/**
 * @author Jackson, Ocelot
 * @since 1.0.0
 */
public interface ModelRegistry {

    /**
     * Registers a single model to be additionally loaded when the resource manager reloads.
     *
     * @param location The location of the model file
     */
    @ExpectPlatform
    static void registerSpecial(ResourceLocation location) {
        Platform.error();
    }

    /**
     * Registers a function for registering as many models as desired each time the resource manager reloads.
     *
     * @param factory The factory for registering models
     * @since 1.2.0
     */
    @ExpectPlatform
    static void registerFactory(ModelFactory factory) {
        Platform.error();
    }

    /**
     * Registers models dynamically each time the resource manager reloads.
     *
     * @since 1.2.0
     */
    @FunctionalInterface
    interface ModelFactory {

        /**
         * Registers models into the specified consumer.
         *
         * @param resourceManager The resource manager for the client
         * @param out             The output for model locations
         */
        void registerModels(ResourceManager resourceManager, Consumer<ResourceLocation> out);
    }
}