package gg.moonflower.pollen.api.event.events.entity.living;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public final class LivingEntityEvents {

    public static final PollinatedEvent<Damage> DAMAGE = EventRegistry.createCancellable(Damage.class);
    public static final PollinatedEvent<Death> DEATH = EventRegistry.createCancellable(Death.class);
    public static final PollinatedEvent<Heal> HEAL = EventRegistry.createCancellable(Heal.class);

    private LivingEntityEvents() {
    }

    /**
     * Fired when an entity is dealt damage.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Damage {

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

    /**
     * Fired when an entity dies.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Death {

        /**
         * Called when the specified entity is about to die.
         *
         * @param entity       The entity that is dying
         * @param damageSource The cause of death
         * @return <code>true</code> to continue, or <code>false</code> to prevent the entity from dying
         */
        boolean death(LivingEntity entity, DamageSource damageSource);
    }

    /**
     * Fired when an entity heals themselves.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Heal {

        /**
         * Called when the specified entity is about to heal.
         *
         * @param entity  The entity being healed
         * @param context The context to change the amount to heal the entity
         * @return <code>true</code> to continue healing the entity, or <code>false</code> to cancel it
         */
        boolean heal(LivingEntity entity, HealContext context);

        interface HealContext {

            float getAmount();

            void setAmount(float amount);
        }
    }
}
