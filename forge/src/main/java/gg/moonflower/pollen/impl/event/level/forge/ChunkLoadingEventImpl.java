package gg.moonflower.pollen.impl.event.level.forge;

import gg.moonflower.pollen.api.event.level.v1.ClientChunkLoadingEvent;
import gg.moonflower.pollen.api.event.level.v1.ServerChunkLoadingEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ChunkLoadingEventImpl {

    @SubscribeEvent
    public static void onEvent(ChunkEvent.Load event) {
        if (event.getLevel().isClientSide()) {
            ClientChunkLoadingEvent.LOAD_CHUNK.invoker().event(event.getLevel(), event.getChunk());
        } else {
            ServerChunkLoadingEvent.LOAD_CHUNK.invoker().event(event.getLevel(), event.getChunk());
        }
    }

    @SubscribeEvent
    public static void onEvent(ChunkEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            ClientChunkLoadingEvent.UNLOAD_CHUNK.invoker().event(event.getLevel(), event.getChunk());
        } else {
            ServerChunkLoadingEvent.UNLOAD_CHUNK.invoker().event(event.getLevel(), event.getChunk());
        }
    }
}
