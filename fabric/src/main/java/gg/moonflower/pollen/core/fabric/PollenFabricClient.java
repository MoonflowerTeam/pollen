package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.event.events.client.render.ReloadRendersEvent;
import gg.moonflower.pollen.api.event.events.entity.EntityEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvent;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvent;
import gg.moonflower.pollen.api.event.events.world.ChunkEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> TickEvent.CLIENT_PRE.invoker().tick());
        ClientTickEvents.END_CLIENT_TICK.register(client -> TickEvent.CLIENT_POST.invoker().tick());
        ClientTickEvents.START_WORLD_TICK.register(TickEvent.LEVEL_PRE.invoker()::tick);
        ClientTickEvents.END_WORLD_TICK.register(TickEvent.LEVEL_POST.invoker()::tick);

        ClientChunkEvents.CHUNK_LOAD.register(ChunkEvents.LOAD.invoker()::load);
        ClientChunkEvents.CHUNK_UNLOAD.register(ChunkEvents.UNLOAD.invoker()::unload);

        InvalidateRenderStateCallback.EVENT.register(() -> ReloadRendersEvent.EVENT.invoker().reloadRenders());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> ClientNetworkEvent.LOGIN.invoker().login(client.gameMode, client.player, handler.getConnection()));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientNetworkEvent.LOGOUT.invoker().logout(client.gameMode, client.player, handler.getConnection()));

        ClientEntityEvents.ENTITY_LOAD.register(EntityEvents.JOIN.invoker()::onJoin);
        ClientEntityEvents.ENTITY_UNLOAD.register(EntityEvents.LEAVE.invoker()::onLeave);
    }
}
