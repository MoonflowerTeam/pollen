package gg.moonflower.pollen.api.event.events.entity;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import gg.moonflower.pollen.api.util.MutableFloat;
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
         * @param entity The entity being hurt
         * @param source The {@link DamageSource} the damage originated from
         * @param damage The amount to damage the entity. Event listeners can modify this
         * @return <code>true</code> to allow damage, or <code>false</code> to prevent it from being dealt
         */
        boolean livingDamage(LivingEntity entity, DamageSource source, MutableFloat damage);
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
         * @param entity The entity being healed
         * @param regen  The amount of health the entity will regenerate. Event listeners can modify this
         * @return <code>true</code> to continue healing the entity, or <code>false</code> to cancel it
         */
        boolean heal(LivingEntity entity, MutableFloat regen);
    }
}