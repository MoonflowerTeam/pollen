package gg.moonflower.pollen.api.event.events.client.resource;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

public final class ModelEvents {

    public static final PollinatedEvent<LoadBlockModel> LOAD_BLOCK_MODEL = EventRegistry.createLoop(LoadBlockModel.class);

    private ModelEvents() {
    }

    public interface LoadBlockModel {
        void load(ResourceLocation location, BlockModel model);
    }
}
