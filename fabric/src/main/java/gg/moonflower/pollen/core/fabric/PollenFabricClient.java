package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.events.client.render.ReloadRendersEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvent;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> EventDispatcher.post(new TickEvent.ClientEvent.Pre()));
        ClientTickEvents.END_CLIENT_TICK.register(client -> EventDispatcher.post(new TickEvent.ClientEvent.Post()));
        ClientTickEvents.START_WORLD_TICK.register(level -> EventDispatcher.post(new TickEvent.LevelEvent.Pre(level)));
        ClientTickEvents.END_WORLD_TICK.register(level -> EventDispatcher.post(new TickEvent.LevelEvent.Post(level)));

        InvalidateRenderStateCallback.EVENT.register(() -> EventDispatcher.post(new ReloadRendersEvent()));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> EventDispatcher.post(new ClientNetworkEvent.LoggedIn(client.gameMode, client.player, handler.getConnection())));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> EventDispatcher.post(new ClientNetworkEvent.LoggedOut(client.gameMode, client.player, handler.getConnection())));
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> EventDispatcher.post(new ClientNetworkEvent.LoggedIn(client.gameMode, client.player, handler.getConnection())));
    }
}
