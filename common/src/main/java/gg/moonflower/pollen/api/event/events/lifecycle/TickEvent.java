package gg.moonflower.pollen.api.event.events.lifecycle;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface TickEvent {

    PollinatedEvent<ClientPre> CLIENT_PRE = EventRegistry.createLoop(ClientPre.class);
    PollinatedEvent<ClientPost> CLIENT_POST = EventRegistry.createLoop(ClientPost.class);

    PollinatedEvent<ServerPre> SERVER_PRE = EventRegistry.createLoop(ServerPre.class);
    PollinatedEvent<ServerPost> SERVER_POST = EventRegistry.createLoop(ServerPost.class);

    PollinatedEvent<LevelPre> LEVEL_PRE = EventRegistry.createLoop(LevelPre.class);
    PollinatedEvent<LevelPost> LEVEL_POST = EventRegistry.createLoop(LevelPost.class);

    PollinatedEvent<LivingPre> LIVING_PRE = EventRegistry.create(LivingPre.class, events -> entity -> {
        for (LivingPre event : events)
            if (event.tick(entity))
                return true;
        return false;
    });

    PollinatedEvent<LivingPost> LIVING_POST = EventRegistry.createLoop(LivingPost.class);

    @FunctionalInterface
    interface ClientPre {
        void tick();
    }

    @FunctionalInterface
    interface ClientPost {
        void tick();
    }

    @FunctionalInterface
    interface ServerPre {
        void tick();
    }

    @FunctionalInterface
    interface ServerPost {
        void tick();
    }

    @FunctionalInterface
    interface LevelPre {
        void tick(Level level);
    }

    @FunctionalInterface
    interface LevelPost {
        void tick(Level level);
    }

    @FunctionalInterface
    interface LivingPre {
        boolean tick(LivingEntity entity);
    }

    @FunctionalInterface
    interface LivingPost {
        void tick(LivingEntity entity);
    }
}
