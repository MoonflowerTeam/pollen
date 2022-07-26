package gg.moonflower.pollen.core.test;

import gg.moonflower.pollen.api.fluid.PollenFluidBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class TestPollenFluidBehavior implements PollenFluidBehavior {

    @Override
    public double getMotionScale(Entity entity) {
        return 0.01;
    }

    @Override
    public boolean canDescend(Player player) {
        return false;
    }

    @Override
    public boolean canAscend(LivingEntity entity) {
        return false;
    }

    @Override
    public boolean canSprint(Player player) {
        return false;
    }

    @Override
    public void applyPhysics(LivingEntity entity, Vec3 travelVector, double fallSpeed, boolean falling) {
        double e = entity.getY();
        float g = 0.02F;

        entity.moveRelative(g, travelVector);
        travelVector.multiply(0.4, 0.1, 0.4);
        entity.move(MoverType.SELF, entity.getDeltaMovement());
        Vec3 vec3 = entity.getDeltaMovement();
        if (entity.horizontalCollision && entity.onClimbable())
            vec3 = new Vec3(vec3.x, 0.2, vec3.z);

        entity.setDeltaMovement(vec3.scale(0.001));
        Vec3 vec32 = entity.getFluidFallingAdjustedMovement(fallSpeed, falling, entity.getDeltaMovement());
        entity.setDeltaMovement(vec32);
        if (entity.horizontalCollision && entity.isFree(vec32.x, vec32.y + 0.6F - entity.getY() + e, vec32.z))
            entity.setDeltaMovement(vec32.x, 0.3F, vec32.z);
    }

    @Override
    public void doSplashEffect(Entity entity, RandomSource random) {
    }

    @Nullable
    @Override
    public SoundEvent getAmbientLoop(Player player) {
        return null;
    }
}
