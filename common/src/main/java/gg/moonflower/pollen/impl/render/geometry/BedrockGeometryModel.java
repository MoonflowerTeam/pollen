package gg.moonflower.pollen.impl.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.molangcompiler.api.bridge.MolangJavaFunction;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import gg.moonflower.pinwheel.api.geometry.*;
import gg.moonflower.pinwheel.api.geometry.bone.AnimatedBone;
import gg.moonflower.pinwheel.api.transform.LocatorTransformation;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.FrameTimer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author Ocelot
 */
public class BedrockGeometryModel extends Model implements GeometryModel {

    private static final Logger LOGGER = LogManager.getLogger("MoLang");

    private static final MolangJavaFunction APPROX_EQUALS = context -> {
        if (context.getParameters() <= 1) {
            return 1.0F;
        }

        float first = context.get(0);
        for (int i = 1; i < context.getParameters(); i++) {
            if (Math.abs(context.get(i) - first) > 0.0000001) {
                return 0.0F;
            }
        }
        return 1.0F;
    };
    private static final MolangJavaFunction LAST_FRAME_TIME = context -> {
        FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
        long[] log = frameTimer.getLog();
        int index = (int) Math.min(context.get(0),
                log.length - 1); // Extended from 30 to 240 since that's what FrameTimer stores
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
    private static final MolangJavaFunction AVERAGE_FRAME_TIME = context -> applyFrame((int) context.get(0), stream -> OptionalLong.of(stream.sum())) / context.get(0);
    private static final MolangJavaFunction MAX_FRAME_TIME = context -> applyFrame((int) context.get(0), LongStream::max);
    private static final MolangJavaFunction MIN_FRAME_TIME = context -> applyFrame((int) context.get(0), LongStream::min);
    private static final MolangJavaFunction CAMERA_ROTATION = context -> {
        int param = (int) context.get(0);
        if (param < 0 || param >= 2) {
            throw new MolangRuntimeException("Invalid argument for camera_rotation: " + param);
        }
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        return param == 0 ? camera.getXRot() : camera.getYRot();
    };
    private static final MolangJavaFunction LOG = context -> {
        float value = context.get(0);
        BedrockGeometryModel.LOGGER.info(value);
        return value;
    };

    private static final Vector3f POSITION = new Vector3f();
    private static final Vector3f ROTATION = new Vector3f();
    private static final Vector3f SCALE = new Vector3f();

    private final Map<String, AnimatedBone.AnimationPose> boneTransformations;
    private final GeometryTree tree;

    public BedrockGeometryModel(GeometryModelData model) throws GeometryCompileException {
        super(RenderType::entityCutout);
        Objects.requireNonNull(model, "model");
        this.tree = GeometryTree.create(model);
        this.boneTransformations = this.tree.getBones()
                .stream()
                .collect(Collectors.toUnmodifiableMap(bone -> bone.getBone().name(), unused -> new AnimatedBone.AnimationPose()));
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack,
                               VertexConsumer builder,
                               int packedLight,
                               int packedOverlay,
                               float red,
                               float green,
                               float blue,
                               float alpha) {
    }

    @Override
    public void render(GeometryRenderer renderer, MatrixStack matrixStack) {
        for (AnimatedBone bone : this.tree.getRootBones()) {
            bone.render(renderer, matrixStack);
        }
    }

    @Override
    public @Nullable AnimatedBone getBone(String name) {
        return this.tree.getBone(name);
    }

    @Override
    public Collection<AnimatedBone> getBones() {
        return this.tree.getBones();
    }

    @Override
    public Collection<AnimatedBone> getRootBones() {
        return this.tree.getRootBones();
    }

    @Override
    public @Nullable LocatorTransformation getLocatorTransformation(String name) {
        return this.tree.getLocatorTransformation(name);
    }

    @Override
    public GeometryModelData.Locator[] getLocators() {
        return this.tree.getLocators();
    }

//    @Override
//    public void applyAnimations(float ticks,
//                                MolangRuntime.Builder builder,
//                                List<PlayingAnimation> animations,
//                                float delta) {
//        if (animations.isEmpty()) {
//            return;
//        }
//
//        builder.setQuery("approx_eq", -1, APPROX_EQUALS);
//        builder.setQuery("average_frame_time", () -> {
//            FrameTimer frameTimer = StarfallClient.getInstance().getFrameTimer();
//            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
//        });
//        builder.setQuery("delta_time", StarfallClient.getInstance().getFrameTime());
//        builder.setQuery("life_time", ticks);
//        builder.setQuery("average_frame_time", 1, AVERAGE_FRAME_TIME);
//        builder.setQuery("last_frame_time", () -> {
//            FrameTimer frameTimer = StarfallClient.getInstance().getFrameTimer();
//            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
//        });
//        builder.setQuery("last_frame_time", 1, LAST_FRAME_TIME);
//        builder.setQuery("maximum_frame_time", () -> {
//            FrameTimer frameTimer = StarfallClient.getInstance().getFrameTimer();
//            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
//        });
//        builder.setQuery("maximum_frame_time", 1, MAX_FRAME_TIME);
//        builder.setQuery("minimum_frame_time", () -> {
//            FrameTimer frameTimer = StarfallClient.getInstance().getFrameTimer();
//            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
//        });
//        builder.setQuery("minimum_frame_time", 1, MIN_FRAME_TIME);
//        builder.setQuery("camera_rotation", 1, CAMERA_ROTATION);
//        builder.setQuery("log", 1, LOG);
//
//        this.boneTransformations.values().forEach(AnimatedBone.AnimationPose::identity);
//        MolangCache cache = MOLANG_CACHE.get();
//        MolangRuntime runtime = builder.create();
//        for (PlayingAnimation animation : animations) {
//            AnimationData data = animation.getData();
//            float blendWeight = cache.resolve(runtime, 1, data.getBlendWeight());
//            blendWeight *= animation.getWeight(delta);
//            if (Math.abs(blendWeight) <= 1E-6) { // No need to add if weight is 0
//                continue;
//            }
//
//            float time = animation.getTick(delta) / 20.0F;
//            float animationLength = GeometryModel.getAnimationLength(time, animation);
//            float localAnimationTime = Math.min(time % animationLength, data.getAnimationLength());
//            for (AnimationData.BoneAnimation boneAnimation : data.getBoneAnimations()) {
//                if (this.getBone(boneAnimation.getName()) == null) {
//                    continue;
//                }
//
//                boolean applyPosition = animation.getPositionMask().test(boneAnimation.getName());
//                boolean applyRotation = animation.getRotationMask().test(boneAnimation.getName());
//                boolean applyScale = animation.getScaleMask().test(boneAnimation.getName());
//                if (!(applyPosition || applyRotation || applyScale)) {
//                    continue;
//                }
//
//                POSITION.set(0, 0, 0);
//                ROTATION.set(0, 0, 0);
//                SCALE.set(1, 1, 1);
//                if (applyPosition) {
//                    KeyframeResolver.resolvePosition(localAnimationTime, cache, runtime, boneAnimation, POSITION);
//                }
//                if (applyRotation) {
//                    KeyframeResolver.resolveRotation(localAnimationTime, cache, runtime, boneAnimation, ROTATION);
//                }
//                if (applyScale) {
//                    KeyframeResolver.resolveScale(localAnimationTime, cache, runtime, boneAnimation, SCALE);
//                }
//
//                this.boneTransformations.get(boneAnimation.getName())
//                        .add(POSITION.x() * blendWeight, POSITION.y() * blendWeight, POSITION.z() * blendWeight,
//                                ROTATION.x() * blendWeight, ROTATION.y() * blendWeight, ROTATION.z() * blendWeight,
//                                (SCALE.x() - 1) * blendWeight, (SCALE.y() - 1) * blendWeight, (SCALE.z() - 1) * blendWeight);
//            }
//        }
//        cache.clear();
//        this.boneTransformations.forEach((name, pose) -> {
//            AnimatedBone.AnimationPose p = Objects.requireNonNull(this.tree.getBone(name)).getAnimationPose();
//            p.identity();
//            p.add(pose.position().x(), pose.position().y(), pose.position().z(),
//                    pose.rotation().x(), pose.rotation().y(), pose.rotation().z(),
//                    pose.scale().x() - 1, pose.scale().y() - 1, pose.scale().z() - 1);
//        });
//        this.updateLocators();
//    }

    private static float applyFrame(int count, FrameFunction frameFunction) throws MolangRuntimeException {
        FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
        long[] log = frameTimer.getLog();
        int duration = Math.min(count, log.length - 1); // Extended from 30 to 240 since that's what FrameTimer stores
        if (duration == 0) {
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        }
        if (duration < 0) {
            throw new MolangRuntimeException("Invalid argument for last_frame_time(): " + duration);
        }
        int wrappedIndex = frameTimer.getLogEnd() - duration;
        while (wrappedIndex < 0) {
            wrappedIndex += 240;
        }

        int finalWrappedIndex = wrappedIndex;
        return (float) frameFunction.apply(LongStream.range(0, duration).map(i -> frameTimer.getLog()[frameTimer.wrapIndex((int) (finalWrappedIndex + i))])).orElse(0L) / 1_000_000_000F; // ns to s
    }

    @FunctionalInterface
    private interface FrameFunction {

        OptionalLong apply(LongStream stream) throws MolangRuntimeException;
    }
}
