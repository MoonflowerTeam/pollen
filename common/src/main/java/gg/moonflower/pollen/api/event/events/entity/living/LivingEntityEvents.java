package gg.moonflower.pollen.api.event.events.entity.living;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class LivingEntityEvents {

    public static final PollinatedEvent<LivingDamageEvent> DAMAGE = EventRegistry.create(LivingDamageEvent.class, events -> (entity, damageSource, context) -> {
        for (LivingDamageEvent event : events)
            if (!event.livingDamage(entity, damageSource, context))
                return false;
        return true;
    });

    private LivingEntityEvents() {
    }

    /**
     * Fired when an entity is dealt damage.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface LivingDamageEvent {

        /**
         * Called before an entity receives damage.
         *
         * @param entity  The entity being hurt
         * @param source  The {@link DamageSource} the damage originated from
         * @param context Context to modify the final damage amount
         * @return <code>true</code> to allow damage, or <code>false</code> to prevent it from being dealt
         */
        boolean livingDamage(LivingEntity entity, DamageSource source, Context context);

        /**
         * Context for modifying damage given to an entity.
         *
         * @since 2.0.0
         */
        interface Context {

            /**
             * @return The amount of damage being dealt
             */
            float getDamageAmount();

            /**
             * Sets a new amount of damage for the entity to receive.
             *
             * @param amount The new damage amount
             */
            void setDamageAmount(float amount);
        }
    }

    
}
