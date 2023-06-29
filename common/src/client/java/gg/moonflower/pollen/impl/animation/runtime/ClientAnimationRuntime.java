package gg.moonflower.pollen.impl.animation.runtime;

import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.bridge.MolangJavaFunction;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import gg.moonflower.pollen.api.animation.v1.controller.IdleAnimationController;
import gg.moonflower.pollen.api.animation.v1.controller.PollenAnimationController;
import gg.moonflower.pollen.api.animation.v1.controller.StateAnimationController;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;
import gg.moonflower.pollen.impl.animation.controller.ClientStateAnimationControllerImpl;
import gg.moonflower.pollen.impl.animation.controller.ClientIdleAnimationControllerImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.OptionalLong;
import java.util.stream.LongStream;

@ApiStatus.Internal
public class ClientAnimationRuntime extends CommonAnimationRuntime {

    private static final MolangJavaFunction LAST_FRAME_TIME = context ->
    {
        FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
        long[] log = frameTimer.getLog();
        int index = (int) Math.min(context.get(0), log.length - 1); // Extended from 30 to 240 since that's what FrameTimer stores
        if (index == 0) {
            return (float) log[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        }
        if (index < 0) {
            throw new MolangRuntimeException("Invalid argument for last_frame_time(): " + index);
        }
        int wrappedIndex = frameTimer.getLogEnd() - index;
        while (wrappedIndex < 0) {
            wrappedIndex += 240;
        }
        return (float) log[frameTimer.wrapIndex(wrappedIndex)] / 1_000_000_000F; // ns to s
    };
    private static final MolangJavaFunction AVERAGE_FRAME_TIME = context -> applyFrame("average_frame_time", (int) context.get(0), stream -> OptionalLong.of(stream.sum())) / context.get(0);
    private static final MolangJavaFunction MAX_FRAME_TIME = context -> applyFrame("max_frame_time", (int) context.get(0), LongStream::max);
    private static final MolangJavaFunction MIN_FRAME_TIME = context -> applyFrame("min_frame_time", (int) context.get(0), LongStream::min);
    private static final MolangJavaFunction CAMERA_ROTATION = context ->
    {
        int param = (int) context.get(0);
        if (param < 0 || param >= 2) {
            throw new MolangRuntimeException("Invalid argument for camera_rotation: " + param);
        }
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        return param == 0 ? camera.getXRot() : camera.getYRot();
    };

    @Override
    public void addGlobal(MolangEnvironmentBuilder<?> builder) {
        super.addGlobal(builder);
        builder.setQuery("average_frame_time", () ->
        {
            FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        });
        builder.setQuery("delta_time", Minecraft.getInstance().getFrameTime());
        builder.setQuery("average_frame_time", 1, AVERAGE_FRAME_TIME);
        builder.setQuery("last_frame_time", () ->
        {
            FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        });
        builder.setQuery("last_frame_time", 1, LAST_FRAME_TIME);
        builder.setQuery("maximum_frame_time", () ->
        {
            FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        });
        builder.setQuery("maximum_frame_time", 1, MAX_FRAME_TIME);
        builder.setQuery("minimum_frame_time", () ->
        {
            FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        });
        builder.setQuery("minimum_frame_time", 1, MIN_FRAME_TIME);
        builder.setQuery("camera_rotation", 1, CAMERA_ROTATION);
    }

    @Override
    public void addEntity(MolangEnvironmentBuilder<?> builder, Entity entity, boolean client) {
        super.addEntity(builder, entity, client);

        if (!client) {
            return;
        }

        // Level
        if (entity.level instanceof ClientLevel clientLevel) {
            builder.setQuery("actor_count", MolangExpression.of(() -> (float) clientLevel.getEntityCount()));
        }
        builder.setQuery("time_of_day", MolangExpression.of(() -> entity.level.getTimeOfDay(Minecraft.getInstance().getFrameTime()) / 24_000L));

        // Basic queries
        builder.setQuery("is_first_person", MolangExpression.of(() -> entity == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON));

        // Rotation
        if (entity instanceof LivingEntity livingEntity) {
            builder.setQuery("body_y_rotation", MolangExpression.of(() -> Mth.lerp(Minecraft.getInstance().getFrameTime(), livingEntity.yBodyRotO, livingEntity.yBodyRot)));
        }
        builder.setQuery("head_x_rotation_speed", MolangExpression.of(() -> {
            float partialTicks = Minecraft.getInstance().getFrameTime();
            return entity.getViewXRot(partialTicks) - entity.getViewXRot((float) (partialTicks - 0.1));
        }));
        builder.setQuery("head_y_rotation_speed", MolangExpression.of(() -> {
            float partialTicks = Minecraft.getInstance().getFrameTime();
            return entity.getViewYRot(partialTicks) - entity.getViewYRot((float) (partialTicks - 0.1));
        }));

        // Misc math
        builder.setQuery("distance_from_camera", MolangExpression.of(() -> (float) Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(entity.position())));
        builder.setQuery("camera_distance_range_lerp", 2, context ->
        {
            float first = context.get(0);
            float second = context.get(1);
            if (first == second) {
                return 1.0F;
            }

            float smaller = Math.min(first, second);
            float larger = Math.max(first, second);

            double distance = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(entity.position());
            if (distance <= smaller) {
                return smaller;
            }
            if (distance >= larger) {
                return larger;
            }
            return (float) ((distance - smaller) / (larger - smaller));
        });
        builder.setQuery("lod_index", -1, context ->
        {
            if (context.getParameters() <= 0) {
                return 0;
            }

            double distance = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(entity.position());
            for (int i = 0; i < context.getParameters(); i++) {
                if (distance < context.get(0)) {
                    return i;
                }
            }
            return context.getParameters() - 1;
        });
        builder.setQuery("position", 1, context ->
        {
            int index = (int) context.get(0);
            if (index < 0 || index >= 3) {
                throw new MolangRuntimeException("Invalid argument for position(): " + index);
            }
            float partialTicks = Minecraft.getInstance().getFrameTime();
            return (float) (index == 0 ? entity.getX(partialTicks) : index == 1 ? entity.getY(partialTicks) : entity.getZ(partialTicks));
        });
        builder.setQuery("position_delta", 1, context ->
        {
            int index = (int) context.get(0);
            if (index < 0 || index >= 3) {
                throw new MolangRuntimeException("Invalid argument for position(): " + index);
            }
            return (float) (index == 0 ? entity.getDeltaMovement().x() : index == 1 ? entity.getDeltaMovement().y() : entity.getDeltaMovement().z());
        });
    }

    @Override
    public StateAnimationController createController(AnimationState[] states, MolangRuntime runtime, boolean client) {
        return client ? new ClientStateAnimationControllerImpl(states, runtime) : super.createController(states, runtime, false);
    }

    @Override
    public IdleAnimationController createIdleController(PollenAnimationController controller) {
        return new ClientIdleAnimationControllerImpl(controller);
    }

    private static float applyFrame(String name, int count, FrameFunction terminator) throws MolangRuntimeException {
        FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
        long[] log = frameTimer.getLog();
        int duration = Math.min(count, log.length - 1); // Extended from 30 to 240 since that's what FrameTimer stores
        if (duration == 0) {
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        }
        if (duration < 0) {
            throw new MolangRuntimeException("Invalid argument for " + name + "(): " + duration);
        }
        int wrappedIndex = frameTimer.getLogEnd() - duration;
        while (wrappedIndex < 0) {
            wrappedIndex += 240;
        }

        int finalWrappedIndex = wrappedIndex;
        return (float) terminator.apply(LongStream.range(0, duration).map(i -> frameTimer.getLog()[frameTimer.wrapIndex((int) (finalWrappedIndex + i))])).orElse(0L) / 1_000_000_000F; // ns to s
    }

    @FunctionalInterface
    private interface FrameFunction {
        OptionalLong apply(LongStream stream) throws MolangRuntimeException;
    }
}
