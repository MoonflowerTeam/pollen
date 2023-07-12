package gg.moonflower.pollen.api.registry.render.v1;

import gg.moonflower.pollen.impl.registry.render.ModelRegistryImpl;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.function.Consumer;

/**
 * @since 2.0.0
 */
public interface ModelRegistry {

    /**
     * Registers a single model to be additionally loaded when the resource manager reloads.
     *
     * @param location The location of the model file
     */
    static void registerSpecial(ResourceLocation location) {
        ModelRegistryImpl.registerSpecial(location);
    }

    /**
     * Registers a function for registering as many models as desired each time the resource manager reloads.
     *
     * @param factory The factory for registering models
     * @since 1.2.0
     */
    static void registerFactory(ModelFactory factory) {
        ModelRegistryImpl.registerFactory(factory);
    }

    /**
     * Retrieves a baked model from the model registry using a regular resource location.
     *
     * @param location The location to get the model by
     * @return The model retrieves by the id
     */
    static BakedModel getModel(ResourceLocation location) {
        return ModelRegistryImpl.getModel(location);
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