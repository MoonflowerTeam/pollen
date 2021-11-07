package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.events.TickEvent;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class PollenFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> EventDispatcher.post(new TickEvent.ClientEvent.Pre()));
        ClientTickEvents.END_CLIENT_TICK.register(client -> EventDispatcher.post(new TickEvent.ClientEvent.Post()));
        ClientTickEvents.START_WORLD_TICK.register(level -> EventDispatcher.post(new TickEvent.LevelEvent.Pre(level)));
        ClientTickEvents.END_WORLD_TICK.register(level -> EventDispatcher.post(new TickEvent.LevelEvent.Post(level)));
    }
}
