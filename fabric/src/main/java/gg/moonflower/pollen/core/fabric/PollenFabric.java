package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.events.ServerLifecycleEvent;
import gg.moonflower.pollen.api.event.events.TickEvent;
import gg.moonflower.pollen.core.Pollen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Pollen.PLATFORM.setup();

        ServerTickEvents.START_SERVER_TICK.register(level -> EventDispatcher.post(new TickEvent.ServerEvent.Pre()));
        ServerTickEvents.END_SERVER_TICK.register(level -> EventDispatcher.post(new TickEvent.ServerEvent.Post()));
        ServerTickEvents.START_WORLD_TICK.register(level -> EventDispatcher.post(new TickEvent.LevelEvent.Pre(level)));
        ServerTickEvents.END_WORLD_TICK.register(level -> EventDispatcher.post(new TickEvent.LevelEvent.Post(level)));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> EventDispatcher.post(new ServerLifecycleEvent.Starting(server)));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> EventDispatcher.post(new ServerLifecycleEvent.Started(server)));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> EventDispatcher.post(new ServerLifecycleEvent.Stopping(server)));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> EventDispatcher.post(new ServerLifecycleEvent.Stopped(server)));
    }
}
