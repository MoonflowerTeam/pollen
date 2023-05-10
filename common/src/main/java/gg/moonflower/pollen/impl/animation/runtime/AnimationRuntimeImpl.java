package gg.moonflower.pollen.impl.animation.runtime;

import gg.moonflower.pollen.impl.platform.InvalidSidedPlatformImpl;
import gg.moonflower.pollen.impl.platform.SidedPlatformImpl;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import io.github.ocelot.molangcompiler.api.bridge.MolangJavaFunction;
import io.github.ocelot.molangcompiler.api.exception.MolangException;
import net.minecraft.util.FrameTimer;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.OptionalLong;
import java.util.ServiceLoader;
import java.util.stream.LongStream;

@ApiStatus.Internal
public class AnimationRuntimeImpl {

    private static final SidedAnimationRuntime PLATFORM = ServiceLoader.load(SidedAnimationRuntime.class).findFirst().orElseGet(CommonAnimationRuntime::new);

    private static final MolangJavaFunction APPROX_EQUALS = context ->
    {
        if (context.getParameters() <= 1)
            return 1.0F;

        float first = context.resolve(0);
        for (int i = 1; i < context.getParameters(); i++)
            if (Math.abs(context.resolve(i) - first) > 0.0000001)
                return 0.0F;
        return 1.0F;
    };
    private static final MolangJavaFunction LAST_FRAME_TIME = context ->
    {
        FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
        long[] log = frameTimer.getLog();
        int index = (int) Math.min(context.resolve(0), log.length - 1); // Extended from 30 to 240 since that's what FrameTimer stores
        if (index == 0)
            return (float) log[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        if (index < 0)
            throw new MolangException("Invalid argument for last_frame_time(): " + index);
        int wrappedIndex = frameTimer.getLogEnd() - index;
        while (wrappedIndex < 0)
            wrappedIndex += 240;
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
    private static final MolangJavaFunction LOG = context ->
    {
        float value = context.resolve(0);
        LOGGER.info(value);
        return value;
    };

    static void addClient(MolangRuntime.Builder builder) {

    }

    static void addEntity(MolangRuntime.Builder builder, Entity entity, boolean client) {

    }

    private static void addEntityClient(Entity entity) {

    }
}
