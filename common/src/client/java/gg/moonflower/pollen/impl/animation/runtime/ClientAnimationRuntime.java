package gg.moonflower.pollen.impl.animation.runtime;

import io.github.ocelot.molangcompiler.api.MolangRuntime;
import io.github.ocelot.molangcompiler.api.bridge.MolangJavaFunction;
import io.github.ocelot.molangcompiler.api.exception.MolangException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FrameTimer;
import net.minecraft.world.entity.Entity;

import java.util.OptionalLong;
import java.util.stream.LongStream;

public class ClientAnimationRuntime extends CommonAnimationRuntime {

    private static final MolangJavaFunction LAST_FRAME_TIME = context ->
    {
        FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
        long[] log = frameTimer.getLog();
        int index = (int) Math.min(context.resolve(0), log.length - 1); // Extended from 30 to 240 since that's what FrameTimer stores
        if (index == 0) {
            return (float) log[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        }
        if (index < 0) {
            throw new MolangException("Invalid argument for last_frame_time(): " + index);
        }
        int wrappedIndex = frameTimer.getLogEnd() - index;
        while (wrappedIndex < 0) {
            wrappedIndex += 240;
        }
        return (float) log[frameTimer.wrapIndex(wrappedIndex)] / 1_000_000_000F; // ns to s
    };
    private static final MolangJavaFunction AVERAGE_FRAME_TIME = context -> applyFrame((int) context.resolve(0), stream -> OptionalLong.of(stream.sum())) / context.resolve(0);
    private static final MolangJavaFunction MAX_FRAME_TIME = context -> applyFrame((int) context.resolve(0), LongStream::max);
    private static final MolangJavaFunction MIN_FRAME_TIME = context -> applyFrame((int) context.resolve(0), LongStream::min);
    private static final MolangJavaFunction CAMERA_ROTATION = context ->
    {
        int param = (int) context.resolve(0);
        if (param < 0 || param >= 2)
            throw new MolangException("Invalid argument for camera_rotation: " + param);
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        return param == 0 ? camera.getXRot() : camera.getYRot();
    };

    @Override
    public void addGlobal(MolangRuntime.Builder builder) {
        super.addGlobal(builder);
        builder.setQuery("average_frame_time", () ->
        {
            FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        });
        builder.setQuery("delta_time", Minecraft.getInstance().getFrameTime());
//        builder.setQuery("life_time", animationTime); TODO
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
    public void addEntity(MolangRuntime.Builder builder, Entity entity, boolean client) {
        super.addEntity(builder, entity, client);

        if (!client) {
            return;
        }


    }

    private static float applyFrame(int count, FrameFunction terminator) throws MolangException {
        FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
        long[] log = frameTimer.getLog();
        int duration = Math.min(count, log.length - 1); // Extended from 30 to 240 since that's what FrameTimer stores
        if (duration == 0) {
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        }
        if (duration < 0) {
            throw new MolangException("Invalid argument for last_frame_time(): " + duration);
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
        OptionalLong apply(LongStream stream) throws MolangException;
    }
}
