package gg.moonflower.pollen.impl.animation.runtime;

import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.bridge.MolangJavaFunction;
import gg.moonflower.molangcompiler.api.bridge.MolangVariableProvider;
import gg.moonflower.pollen.api.animation.v1.controller.StateAnimationController;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;
import gg.moonflower.pollen.impl.animation.controller.StateAnimationControllerImpl;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public class CommonAnimationRuntime implements SidedAnimationRuntime {

    private static final Logger LOGGER = LoggerFactory.getLogger("MoLang");

    private static final MolangJavaFunction APPROX_EQUALS = context ->
    {
        if (context.getParameters() <= 1) {
            return 1.0F;
        }

        float first = context.get(0);
        for (int i = 1; i < context.getParameters(); i++) {
            if (Math.abs(context.get(i) - first) > 1.0E-6) {
                return 0.0F;
            }
        }
        return 1.0F;
    };
    private static final MolangJavaFunction LOG = context ->
    {
        int size = context.getParameters();
        if (size == 1) {
            float value = context.get(0);
            LOGGER.info(String.valueOf(value));
            return value;
        }

        String[] values = new String[size];
        for (int i = 0; i < values.length; i++) {
            values[i] = Float.toString(context.get(i));
        }
        LOGGER.info(String.join(", ", values));
        return 0.0F;
    };

    @Override
    public void addGlobal(MolangEnvironmentBuilder<?> builder) {
        builder.setQuery("approx_eq", -1, APPROX_EQUALS);
        builder.setQuery("log", -1, LOG);
    }

    @Override
    public void addEntity(MolangEnvironmentBuilder<?> builder, Entity entity, boolean client) {
        if (entity instanceof MolangVariableProvider provider) {
            builder.setVariables(provider);
        }

        builder.setQuery("day", MolangExpression.of(() -> (float) (entity.level.getDayTime() / 24000L + 1)));
        builder.setQuery("moon_phase", MolangExpression.of(() -> (float) entity.level.getMoonPhase()));
        builder.setQuery("moon_brightness", MolangExpression.of(() -> entity.level.getMoonBrightness()));

        // Basic queries
        builder.setQuery("is_on_ground", MolangExpression.of(entity::isOnGround));
        builder.setQuery("is_in_water", MolangExpression.of(entity::isInWater));
        builder.setQuery("is_in_water_or_rain", MolangExpression.of(entity::isInWaterOrRain));
        builder.setQuery("is_in_contact_with_water", MolangExpression.of(entity::isInWaterRainOrBubble));
        builder.setQuery("is_moving", MolangExpression.of(() -> entity.getDeltaMovement().lengthSqr() > 1.0E-7D));
        builder.setQuery("is_alive", MolangExpression.of(entity::isAlive));
        builder.setQuery("is_fire_immune", MolangExpression.of(entity::fireImmune));
        builder.setQuery("is_on_fire", MolangExpression.of(entity::isOnFire));
        builder.setQuery("is_invisible", MolangExpression.of(entity::isInvisible));
        builder.setQuery("is_ghost", MolangExpression.of(entity::isSpectator));

        // Speed
        builder.setQuery("ground_speed", MolangExpression.of(() -> {
            Vec3 velocity = entity.getDeltaMovement();
            return Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
        }));
        if (entity instanceof LivingEntity livingEntity) {
            builder.setQuery("modified_move_speed", MolangExpression.of(livingEntity::getSpeed));
        }
        builder.setQuery("modified_distance_moved", MolangExpression.of(() -> entity.moveDist));
        builder.setQuery("vertical_speed", MolangExpression.of(() -> (float) entity.getDeltaMovement().y()));

        // Living specific properties
        if (entity instanceof LivingEntity livingEntity) {
            builder.setQuery("health", MolangExpression.of(livingEntity::getHealth));
            builder.setQuery("max_health", MolangExpression.of(livingEntity::getMaxHealth));
            builder.setQuery("is_baby", MolangExpression.of(livingEntity::isBaby));
        }
    }

    @Override
    public StateAnimationController createController(AnimationState[] states, MolangRuntime runtime, boolean client) {
        return new StateAnimationControllerImpl(states, runtime);
    }
}
