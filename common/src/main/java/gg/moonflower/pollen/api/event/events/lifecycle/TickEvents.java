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

    private TickEvents() {}

    @FunctionalInterface
    public interface ClientPre {
        void tick();
    }

    @FunctionalInterface
    public interface ClientPost {
        void tick();
    }

    @FunctionalInterface
    public interface ServerPre {
        void tick();
    }

    @FunctionalInterface
    public interface ServerPost {
        void tick();
    }

    @FunctionalInterface
    public interface LevelPre {
        void tick(Level level);
    }

    @FunctionalInterface
    public interface LevelPost {
        void tick(Level level);
    }

    @FunctionalInterface
    public interface LivingPre {
        boolean tick(LivingEntity entity);
    }

    @FunctionalInterface
    public interface LivingPost {
        void tick(LivingEntity entity);
    }

}