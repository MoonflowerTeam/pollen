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

    private ServerLifecycleEvents() {
    }

    /**
     * Fired when the server is about to load the level.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface PreStart {

        /**
         * Called just before the server loads the level.
         *
         * @param server The server currently starting
         * @return <code>true</code> to continue server start, <code>false</code> to stop the server from starting
         */
        boolean preStarting(MinecraftServer server);
    }

    /**
     * Fired when the server is currently starting after loading the level and info.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Starting {

        /**
         * Called when the server is currently starting after loading the level and info.
         *
         * @param server The server currently starting
         * @return <code>true</code> to continue server start, <code>false</code> to stop the server from starting
         */
        boolean starting(MinecraftServer server);
    }

    /**
     * Fired after the server has fully started.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Started {

        /**
         * Called when the specified server has started.
         *
         * @param server The server started
         */
        void started(MinecraftServer server);
    }

    /**
     * Fired when the server starts to stop.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Stopping {

        /**
         * Called when the specified server has started to stop.
         *
         * @param server The server stopping
         */
        void stopping(MinecraftServer server);
    }

    /**
     * Fired after the server has fully stopped.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface Stopped {

        /**
         * Called when the specified server has stopped.
         *
         * @param server The server stopped
         */
        void stopped(MinecraftServer server);
    }
}