package gg.moonflower.pollen.api.events.v1.client.resource;

import gg.moonflower.pollen.api.base.event.EventRegistry;
import gg.moonflower.pollen.api.base.event.PollinatedEvent;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

public final class ModelEvents {

    public static final PollinatedEvent<LoadBlockModel> LOAD_BLOCK_MODEL = EventRegistry.createLoop(LoadBlockModel.class);

    private ModelEvents() {
    }

    /**
     * Fired each time a new block model is loaded.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface LoadBlockModel {

        /**
         * Called when a block model is loaded.
         *
         * @param location The location of the model
         * @param model    The model loaded
         */
        void load(ResourceLocation location, BlockModel model);
    }
}
