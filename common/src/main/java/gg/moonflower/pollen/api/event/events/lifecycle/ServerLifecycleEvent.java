package gg.moonflower.pollen.api.event.events.lifecycle;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import net.minecraft.server.MinecraftServer;

/**
 * Events fired for server lifecycle.
 */
public class ServerLifecycleEvent implements PollinatedEvent {

    private final MinecraftServer server;

    protected ServerLifecycleEvent(MinecraftServer server) {
        this.server = server;
    }

    /**
     * @return The main server instance
     */
    public MinecraftServer getServer() {
        return server;
    }

    /**
     * Called at the beginning of the server start phase.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Starting extends ServerLifecycleEvent {

        public Starting(MinecraftServer server) {
            super(server);
        }
    }

    /**
     * Called after the server fully loads.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Started extends ServerLifecycleEvent {

        public Started(MinecraftServer server) {
            super(server);
        }
    }

    /**
     * Called at the beginning of the server stop phase.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Stopping extends ServerLifecycleEvent {

        public Stopping(MinecraftServer server) {
            super(server);
        }
    }

    /**
     * Called after the server fully stops.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Stopped extends ServerLifecycleEvent {

        public Stopped(MinecraftServer server) {
            super(server);
        }
    }
}
