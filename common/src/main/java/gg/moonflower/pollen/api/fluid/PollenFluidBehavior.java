package gg.moonflower.pollen.api.fluid;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * Hooks for specifying exactly how a fluid should interact with entities.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollenFluidBehavior {

    /**
     * @param player The player trying to descend
     * @return Whether that player can descend by holding sneak in this fluid
     */
    default boolean canDescend(Player player) {
        return true;
    }

    /**
     * @param entity The entity trying to ascend
     * @return Whether that entity can ascend by holding jump in this fluid
     */
    default boolean canAscend(LivingEntity entity) {
        return true;
    }

    /**
     * @param player The player trying to sprint
     * @return Whether that player can sprint in this fluid
     */
    default boolean canSprint(Player player) {
        return true;
    }

    /**
     * @param entity The entity to check
     * @return Whether the specified entity should drown in this fluid
     */
    default boolean shouldEntityDrown(LivingEntity entity) {
        return !entity.level.getBlockState(entity.blockPosition()).is(Blocks.BUBBLE_COLUMN);
    }

    /**
     * @param entity  The entity to check
     * @param vehicle The vehicle the entity is riding
     * @return Whether the specified vehicle can be ridden in this fluid
     */
    default boolean canVehicleTraverse(LivingEntity entity, Entity vehicle) {
        return vehicle.rideableUnderWater();
    }

    /**
     * @param entity The entity drowning
     * @return The particle to use while drowning or <code>null</code> to remove particles
     */
    @Nullable
    default ParticleOptions getDrowningParticles(LivingEntity entity) {
        return ParticleTypes.BUBBLE;
    }

    /**
     * @param entity The entity drowning
     * @return The amount of damage to deal to the specified entity while drowning in this fluid
     */
    default float getDrowningDamage(LivingEntity entity) {
        return 2.0F;
    }

    /**
     * @param entity The entity to check
     * @return Whether this fluid blocks all fall damage like water
     */
    default boolean negatesFallDamage(Entity entity) {
        return true;
    }

    /**
     * @param entity The entity to check
     * @return Whether this fluid extinguishes fire like water
     */
    default boolean canExtinguishFire(Entity entity) {
        return true;
    }

    /**
     * @param entity The entity to check
     * @return The amount to move the specified entity each tick while in this fluid
     */
    default double getMotionScale(Entity entity) {
        return 0.014;
    }

    /**
     * @param entity The entity to check
     * @return The amount of speed to move the entity at while in this fluid
     */
    default double getSlowDown(LivingEntity entity) {
        return entity.isSprinting() ? 0.9 : 0.8;
    }

    /**
     * @param player The player to get the sound for
     * @return The sound to play for the local player when entering this fluid or <code>null</code> for no sound
     */
    @Nullable
    default SoundEvent getAmbientEnter(Player player) {
        return SoundEvents.AMBIENT_UNDERWATER_ENTER;
    }

    /**
     * @param player The player to get the sound for
     * @return The sound to loop for the local player when in this fluid or <code>null</code> for no sound
     */
    @Nullable
    default SoundEvent getAmbientLoop(Player player) {
        return SoundEvents.AMBIENT_UNDERWATER_LOOP;
    }

    /**
     * @param player The player to get the sound for
     * @return The sound to play for the local player when exiting this fluid or <code>null</code> for no sound
     */
    @Nullable
    default SoundEvent getAmbientExit(Player player) {
        return SoundEvents.AMBIENT_UNDERWATER_EXIT;
    }

    /**
     * Applies the physics of this fluid on the specified entity.
     *
     * @param entity       The entity to apply to
     * @param travelVector The direction the entity wishes to travel in
     * @param fallSpeed    The speed the entity should fall at
     * @param falling      Whether the entity is travelling down
     */
    default void applyPhysics(LivingEntity entity, Vec3 travelVector, double fallSpeed, boolean falling) {
        double e = entity.getY();
        double f = this.getSlowDown(entity);
        float g = 0.02F;
        float h = (float) EnchantmentHelper.getDepthStrider(entity);
        if (h > 3.0F)
            h = 3.0F;

        if (!entity.isOnGround())
            h *= 0.5F;

        if (h > 0.0F) {
            f += (0.54600006F - f) * h / 3.0F;
            g += (entity.getSpeed() - g) * h / 3.0F;
        }

        if (entity.hasEffect(MobEffects.DOLPHINS_GRACE))
            f = 0.96F;

        entity.moveRelative(g, travelVector);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        Vec3 vec3 = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.onClimbable())
            vec3 = new Vec3(vec3.x, 0.2, vec3.z);

        entity.setDeltaMovement(vec3.multiply(f, 0.8F, f));
        Vec3 vec32 = entity.getFluidFallingAdjustedMovement(fallSpeed, falling, entity.getDeltaMovement());
        entity.setDeltaMovement(vec32);
        if (entity.horizontalCollision && entity.isFree(vec32.x, vec32.y + 0.6F - entity.getY() + e, vec32.z))
            entity.setDeltaMovement(vec32.x, 0.3F, vec32.z);
    }

    /**
     * Applies the liquid splash effect. The default implementation mimics water.
     *
     * @param entity The entity splashing
     * @param random The random instance for effects
     */
    default void doSplashEffect(Entity entity, RandomSource random) {
        Entity vehicle = entity.isVehicle() && entity.getControllingPassenger() != null ? entity.getControllingPassenger() : entity;
        float f = vehicle == entity ? 0.2F : 0.9F;
        Vec3 vec3 = vehicle.getDeltaMovement();
        float g = Mth.sqrt((float) (vec3.x * vec3.x * 0.2F + vec3.y * vec3.y + vec3.z * vec3.z * 0.2F)) * f;
        if (g > 1.0F) {
            g = 1.0F;
        }

        if (g < 0.25) {
            entity.playSound(entity.getSwimSplashSound(), g, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
        } else {
            entity.playSound(entity.getSwimHighSpeedSplashSound(), g, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
        }

        float h = Mth.floor(entity.getY());

        for (int i = 0; i < 1.0F + entity.getBbWidth() * 20.0F; ++i) {
            double d = (random.nextDouble() * 2.0 - 1.0) * entity.getBbWidth();
            double e = (random.nextDouble() * 2.0 - 1.0) * entity.getBbWidth();
            entity.level.addParticle(ParticleTypes.BUBBLE, entity.getX() + d, h + 1.0F, entity.getZ() + e, vec3.x, vec3.y - random.nextDouble() * 0.2F, vec3.z);
        }

        for (int i = 0; i < 1.0F + entity.getBbWidth() * 20.0F; ++i) {
            double d = (random.nextDouble() * 2.0 - 1.0) * entity.getBbWidth();
            double e = (random.nextDouble() * 2.0 - 1.0) * entity.getBbWidth();
            entity.level.addParticle(ParticleTypes.SPLASH, entity.getX() + d, h + 1.0F, entity.getZ() + e, vec3.x, vec3.y, vec3.z);
        }
    }
}
