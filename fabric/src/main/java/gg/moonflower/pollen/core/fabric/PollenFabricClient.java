package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.event.level.v1.ClientChunkLoadingEvent;
import gg.moonflower.pollen.api.event.network.v1.ClientNetworkEvent;
import gg.moonflower.pollen.core.PollenClient;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.network.fabric.FabricClientLoginPacketHandlerImpl;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class PollenFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PollenClient.init();
        PollenClient.postInit();

        PollenMessages.LOGIN.setClientHandler(new FabricClientLoginPacketHandlerImpl());

        ClientChunkEvents.CHUNK_LOAD.register((level, chunk) -> ClientChunkLoadingEvent.LOAD_CHUNK.invoker().event(level, chunk));
        ClientChunkEvents.CHUNK_UNLOAD.register((level, chunk) -> ClientChunkLoadingEvent.UNLOAD_CHUNK.invoker().event(level, chunk));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientNetworkEvent.LOGIN.invoker().event(client.gameMode, client.player, handler.getConnection()));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientNetworkEvent.DISCONNECT.invoker().event(client.gameMode, client.player, handler.getConnection()));
    }
}
