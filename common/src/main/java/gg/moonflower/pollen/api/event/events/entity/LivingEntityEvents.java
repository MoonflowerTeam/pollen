package gg.moonflower.pollen.api.event.events.entity;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import gg.moonflower.pollen.api.util.MutableBoolean;
import gg.moonflower.pollen.api.util.MutableFloat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public final class LivingEntityEvents {

    public static final PollinatedEvent<ShieldBlock> SHIELD_BLOCK = EventRegistry.createCancellable(ShieldBlock.class);
    public static final PollinatedEvent<Damage> DAMAGE = EventRegistry.createCancellable(Damage.class);
    public static final PollinatedEvent<Death> DEATH = EventRegistry.createCancellable(Death.class);
    public static final PollinatedEvent<Heal> HEAL = EventRegistry.createCancellable(Heal.class);

    private LivingEntityEvents() {
    }

    /**
     * Fired when an entity successfully blocks with a shield.
     *
     * @author ebo2022
     * @since
     */
    @FunctionalInterface
    public interface ShieldBlock {

        /**
         * Called when an entity has blocked with a shield.
         *
         * @param entity                The entity holding a shield
         * @param damageSource          The source of the incoming damage
         * @param originalBlockedDamage The original amount of damage blocked, equal to the incoming damage
         * @param blockedDamage         The amount of damage to block. Event listeners can modify this
         * @param loseDurability        Whether the shield will take durability damage. Event listeners can modify this
         * @return <code>false</code> if the shield should not be eligible to work
         */
        boolean onShieldBlock(Entity entity, DamageSource damageSource, float originalBlockedDamage, MutableFloat blockedDamage, MutableBoolean loseDurability);
    }

    /**
     * Fired when an entity is dealt damage.
     *
     * @author ebo2022
     * @since
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
     * @since
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
     * @since
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