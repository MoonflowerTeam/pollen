package gg.moonflower.pollen.api.event.events.lifecycle;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Events fired for game updates.
 */
public class TickEvent extends PollinatedEvent {

    /**
     * Called each time the client runs a tick. Use {@link .Pre} and {@link Post} for specific tick timeframes.
     *
     * <p><b><i>NOTE: This is only when the client runs a tick 20 times per second, not a render frame.</i></b>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class ClientEvent extends TickEvent {

        private ClientEvent() {
        }

        public static class Pre extends ClientEvent {
        }

        public static class Post extends ClientEvent {
        }
    }

    /**
     * Called each time the server runs a tick. Use {@link Pre} and {@link Post} for specific tick timeframes.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class ServerEvent extends TickEvent {

        private ServerEvent() {
        }

        public static class Pre extends ServerEvent {
        }

        public static class Post extends ServerEvent {
        }
    }

    /**
     * Called each time a living entity is ticked server and client side. Use {@link Pre} and {@link Post} for specific tick timeframes.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class LivingEntityEvent extends TickEvent {

        private final LivingEntity entity;

        private LivingEntityEvent(LivingEntity entity) {
            this.entity = entity;
        }

        /**
         * @return The entity being ticked
         */
        public Entity getEntity() {
            return entity;
        }

        @Cancellable
        public static class Pre extends LivingEntityEvent {
            public Pre(LivingEntity entity) {
                super(entity);
            }
        }

        public static class Post extends LivingEntityEvent {
            public Post(LivingEntity entity) {
                super(entity);
            }
        }
    }

    /**
     * Called each time a level is ticked server and client side. Use {@link Pre} and {@link Post} for specific tick timeframes.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class LevelEvent extends TickEvent {

        private final Level level;

        private LevelEvent(Level level) {
            this.level = level;
        }

        /**
         * @return The level being ticked
         */
        public Level getLevel() {
            return level;
        }

        public static class Pre extends LevelEvent {
            public Pre(Level level) {
                super(level);
            }
        }

        public static class Post extends LevelEvent {
            public Post(Level level) {
                super(level);
            }
        }
    }

    /**
     * Called each time an entity is ticked server and client side. Use {@link Pre} and {@link Post} for specific tick timeframes.
     *
     * @author abigailfails
     * @since 1.0.0
     */
    public static class EntityEvent extends TickEvent {

        private final Entity entity;

        public EntityEvent(Entity entity) {
            this.entity = entity;
        }

        /**
         * @return The entity being ticked
         */
        public Entity getEntity() {
            return entity;
        }

        public static class Pre extends EntityEvent {
            public Pre(Entity entity) {
                super(entity);
            }
        }

        public static class Post extends EntityEvent {
            public Post(Entity entity) {
                super(entity);
            }
        }
    }
}
