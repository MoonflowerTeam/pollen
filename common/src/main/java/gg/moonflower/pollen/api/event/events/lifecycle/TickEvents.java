package gg.moonflower.pollen.api.event.events.lifecycle;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public final class TickEvents {

    public static final PollinatedEvent<ClientPre> CLIENT_PRE = EventRegistry.createLoop(ClientPre.class);
    public static final PollinatedEvent<ClientPost> CLIENT_POST = EventRegistry.createLoop(ClientPost.class);

    public static final PollinatedEvent<ServerPre> SERVER_PRE = EventRegistry.createLoop(ServerPre.class);
    public static final PollinatedEvent<ServerPost> SERVER_POST = EventRegistry.createLoop(ServerPost.class);

    public static final PollinatedEvent<LevelPre> LEVEL_PRE = EventRegistry.createLoop(LevelPre.class);
    public static final PollinatedEvent<LevelPost> LEVEL_POST = EventRegistry.createLoop(LevelPost.class);

    public static final PollinatedEvent<LivingPre> LIVING_PRE = EventRegistry.create(LivingPre.class, events -> entity -> {
        for (LivingPre event : events)
            if (!event.tick(entity))
                return false;
        return true;
    });
    public static final PollinatedEvent<LivingPost> LIVING_POST = EventRegistry.createLoop(LivingPost.class);

    private TickEvents() {
    }

    /**
     * Fired on the client side at the start of the tick.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface ClientPre {

        /**
         * Called at the start of the client side tick.
         */
        void tick();
    }

    /**
     * Fired on the client side at the end of the tick.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface ClientPost {

        /**
         * Called at the end of the client side tick.
         */
        void tick();
    }

    /**
     * Fired on the server side at the start of the tick.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface ServerPre {

        /**
         * Called at the start of the server side tick.
         */
        void tick();
    }

    /**
     * Fired on the server side at the end of the tick.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface ServerPost {

        /**
         * Called at the end of the server side tick.
         */
        void tick();
    }

    /**
     * Fired on both sides at the start of the level tick.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface LevelPre {

        /**
         * Called at the start of the specified level tick.
         *
         * @param level The level ticking
         */
        void tick(Level level);
    }

    /**
     * Fired on both sides at the end of the level tick.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface LevelPost {

        /**
         * Called at the end of the specified level tick.
         *
         * @param level The level ticking
         */
        void tick(Level level);
    }

    /**
     * Fired on both sides at the start of the living entity tick.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface LivingPre {

        /**
         * Called at the start of the specified entity tick.
         *
         * @param entity The entity ticking
         */
        boolean tick(LivingEntity entity);
    }

    /**
     * Fired on both sides at the end of the living entity tick.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface LivingPost {

        /**
         * Called at the end of the specified entity tick.
         *
         * @param entity The entity ticking
         */
        void tick(LivingEntity entity);
    }
}