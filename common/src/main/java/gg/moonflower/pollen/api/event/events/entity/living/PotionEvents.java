package gg.moonflower.pollen.api.event.events.entity.living;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public final class PotionEvents {

    public static final PollinatedEvent<Expire> EXPIRE = EventRegistry.createLoop(Expire.class);

    private PotionEvents() {
    }

    public interface Applicable {

    }

    @FunctionalInterface
    public interface Add {


        void add(LivingEntity entity, MobEffectInstance oldEffectInstance, MobEffectInstance newEffectDistance);
    }

    public interface Remove {

    }

    /**
     * Fired when an effect expires on an entity.
     */
    @FunctionalInterface
    public interface Expire {

        /**
         * Called when the specified effect has expired on the given entity.
         *
         * @param entity         The entity that had the effect
         * @param effectInstance An instance of the effect that expired
         */
        void expire(LivingEntity entity, MobEffectInstance effectInstance);
    }
}
