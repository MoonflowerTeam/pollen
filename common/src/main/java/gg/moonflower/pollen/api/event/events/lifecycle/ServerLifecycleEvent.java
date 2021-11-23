package gg.moonflower.pollen.api.event.events.lifecycle;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.server.MinecraftServer;

public interface ServerLifecycleEvent {

    PollinatedEvent<Starting> STARTING = EventRegistry.createLoop(Starting.class);
    PollinatedEvent<Started> STARTED = EventRegistry.createLoop(Started.class);
    PollinatedEvent<Stopping> STOPPING = EventRegistry.createLoop(Stopping.class);
    PollinatedEvent<Stopped> STOPPED = EventRegistry.createLoop(Stopped.class);

    @FunctionalInterface
    interface Starting {
        void starting(MinecraftServer server);
    }

    @FunctionalInterface
    interface Started {
        void started(MinecraftServer server);
    }

    @FunctionalInterface
    interface Stopping {
        void stopping(MinecraftServer server);
    }

    @FunctionalInterface
    interface Stopped {
        void stopped(MinecraftServer server);
    }

}