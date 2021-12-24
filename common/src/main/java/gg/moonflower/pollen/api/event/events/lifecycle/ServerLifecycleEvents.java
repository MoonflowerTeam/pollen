package gg.moonflower.pollen.api.event.events.lifecycle;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.server.MinecraftServer;

public final class ServerLifecycleEvents {

    public static final PollinatedEvent<PreStart> PRE_STARTING = EventRegistry.create(PreStart.class, listeners -> server -> {
        for (PreStart listener : listeners)
            if (!listener.preStarting(server))
                return false;
        return true;
    });
    public static final PollinatedEvent<Starting> STARTING = EventRegistry.create(Starting.class, listeners -> server -> {
        for (Starting listener : listeners)
            if (!listener.starting(server))
                return false;
        return true;
    });
    public static final PollinatedEvent<Started> STARTED = EventRegistry.createLoop(Started.class);
    public static final PollinatedEvent<Stopping> STOPPING = EventRegistry.createLoop(Stopping.class);
    public static final PollinatedEvent<Stopped> STOPPED = EventRegistry.createLoop(Stopped.class);

    @FunctionalInterface
    public interface PreStart {

        /**
         * Fired when the server is about to load the level.
         *
         * @param server The server currently starting
         * @return <code>true</code> to continue server start, <code>false</code> to stop the server from starting
         */
        boolean preStarting(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Starting {

        /**
         * Fired when the server is currently starting after loading the level and info.
         *
         * @param server The server currently starting
         * @return <code>true</code> to continue server start, <code>false</code> to stop the server from starting
         */
        boolean starting(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Started {
        void started(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Stopping {
        void stopping(MinecraftServer server);
    }

    @FunctionalInterface
    public interface Stopped {
        void stopped(MinecraftServer server);
    }

    private ServerLifecycleEvents() {
    }
}