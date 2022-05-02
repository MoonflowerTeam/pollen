package gg.moonflower.pollen.pinwheel.core.client.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimatedModel;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimatedModelPart;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModel;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationData;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelData;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import io.github.ocelot.molangcompiler.api.bridge.MolangJavaFunction;
import io.github.ocelot.molangcompiler.api.exception.MolangException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class BedrockGeometryModel extends Model implements GeometryModel, AnimatedModel {

    private static final Logger LOGGER = LogManager.getLogger("MoLang");
    private static final ThreadLocal<MolangCache> MOLANG_CACHE = ThreadLocal.withInitial(MolangCache::new);

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

    private static final Vector3f POSITION = new Vector3f();
    private static final Vector3f ROTATION = new Vector3f();
    private static final Vector3f SCALE = new Vector3f();

    private final Map<String, AnimatedModelPart.AnimationPose> transformations;
    private final Map<String, BoneModelPart> modelParts;
    private final Set<BoneModelPart> renderParts;
    private final String[] modelKeys;
    private final String[] textureKeys;
    private final int texWidth;
    private final int texHeight;
    private String activeMaterial;

    public BedrockGeometryModel(int textureWidth, int textureHeight, GeometryModelData.Bone[] bones) {
        super(RenderType::entityCutoutNoCull);
        this.texWidth = textureWidth;
        this.texHeight = textureHeight;
        this.transformations = new HashMap<>();
        this.modelParts = new HashMap<>();
        this.renderParts = new HashSet<>();

        Set<String> textures = new HashSet<>();
        for (GeometryModelData.Bone bone : bones) {
            for (GeometryModelData.Cube cube : bone.getCubes()) {
                for (Direction direction : Direction.values()) {
                    GeometryModelData.CubeUV uv = cube.getUV(direction);
                    if (uv == null)
                        continue;
                    textures.add(uv.getMaterialInstance());
                }
            }
            if (bone.getPolyMesh() != null)
                textures.add("poly_mesh.texture");
        }
        this.textureKeys = textures.toArray(new String[0]);

        if (bones.length == 0) {
            this.modelKeys = new String[0];
            return;
        }

        Map<String, Pair<GeometryModelData.Bone, BoneModelPart>> boneLookup = Arrays.stream(bones).map(bone -> Pair.of(bone, new BoneModelPart(this, bone))).collect(Collectors.toMap(pair -> pair.getKey().getName(), pair -> pair));
        Map<GeometryModelData.Bone, String> parts = new HashMap<>();
        List<String> unprocessedBones = Arrays.stream(bones).map(GeometryModelData.Bone::getName).collect(Collectors.toList());

        while (!unprocessedBones.isEmpty()) {
            Pair<GeometryModelData.Bone, BoneModelPart> pair = boneLookup.get(unprocessedBones.remove(0));
            GeometryModelData.Bone currentBone = pair.getLeft();
            String parent = currentBone.getParent();

            if (parent != null) {
                if (parent.startsWith("parent.")) {
                    parts.put(currentBone, parent.substring("parent.".length()));
                } else {
                    if (!boneLookup.containsKey(parent))
                        throw new IllegalStateException("Unknown bone '" + parent + "'");

                    BoneModelPart parentRenderer = boneLookup.get(parent).getRight();
                    parentRenderer.addChild(pair.getRight());
                }
            }

            unprocessedBones.remove(currentBone.getName());
        }

        for (Pair<GeometryModelData.Bone, BoneModelPart> pair : boneLookup.values()) {
            GeometryModelData.Bone currentBone = pair.getLeft();

            this.modelParts.put(currentBone.getName(), pair.getRight());
            if (currentBone.getParent() == null || currentBone.getParent().startsWith("parent.")) {
                this.renderParts.add(pair.getRight());
            }
        }

        this.modelKeys = parts.values().toArray(new String[0]);
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
    }

    @Override
    public void render(String material, GeometryModelTexture texture, PoseStack matrixStack, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.activeMaterial = material;
        for (BoneModelPart part : this.renderParts)
            part.render(matrixStack, builder, packedLight, packedOverlay, red, green, blue, alpha);
        this.activeMaterial = "texture";
    }

    @Override
    public void resetTransformation() {
        this.modelParts.values().forEach(renderer -> renderer.resetTransform(true));
    }

    @Override
    public void copyAngles(@Nullable String parent, ModelPart limbRenderer) {
        this.modelParts.values().stream().filter(part -> Objects.equals(part.getBone().getParent(), parent)).forEach(renderer -> renderer.copyFrom(limbRenderer));
    }

    @Override
    public Optional<ModelPart> getModelPart(String part) {
        return Optional.ofNullable(this.modelParts.get(part));
    }

    @Override
    public ModelPart[] getChildRenderers(String part) {
        return this.modelParts.values().stream().filter(boneModelPart -> part.equals(boneModelPart.getBone().getParent())).toArray(ModelPart[]::new);
    }

    @Override
    public ModelPart[] getModelParts() {
        return this.modelParts.values().toArray(new ModelPart[0]);
    }

    @Override
    public String[] getParentModelKeys() {
        return modelKeys;
    }

    @Override
    public String[] getMaterialKeys() {
        return textureKeys;
    }

    @Override
    public float getTextureWidth() {
        return texWidth;
    }

    @Override
    public float getTextureHeight() {
        return texHeight;
    }

    @Override
    public void applyAnimations(float animationTime, MolangRuntime.Builder runtime, float[] weights, AnimationData... animations) {
        if (animations.length == 0)
            return;

        runtime.setQuery("approx_eq", -1, APPROX_EQUALS);
        runtime.setQuery("average_frame_time", () ->
        {
            FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        });
        runtime.setQuery("delta_time", Minecraft.getInstance().getFrameTime());
        runtime.setQuery("life_time", animationTime);
        runtime.setQuery("average_frame_time", 1, AVERAGE_FRAME_TIME);
        runtime.setQuery("last_frame_time", () ->
        {
            FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        });
        runtime.setQuery("last_frame_time", 1, LAST_FRAME_TIME);
        runtime.setQuery("maximum_frame_time", () ->
        {
            FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        });
        runtime.setQuery("maximum_frame_time", 1, MAX_FRAME_TIME);
        runtime.setQuery("minimum_frame_time", () ->
        {
            FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        });
        runtime.setQuery("minimum_frame_time", 1, MIN_FRAME_TIME);
        runtime.setQuery("camera_rotation", 1, CAMERA_ROTATION);
        runtime.setQuery("log", 1, LOG);

        animationTime %= AnimatedModel.getAnimationLength(animationTime, animations);

        this.transformations.values().forEach(AnimatedModelPart.AnimationPose::reset);
        MolangCache cache = MOLANG_CACHE.get();
        for (int i = 0; i < animations.length; i++) {
            AnimationData animation = animations[i];
            float blendWeight = cache.resolve(runtime, 1, animation.getBlendWeight());
            if (i < weights.length)
                blendWeight *= weights[i];
            if (Math.abs(blendWeight) <= 1E-6) // No need to add if weight is 0
                continue;

            float localAnimationTime = Math.min(animationTime, animation.getAnimationLength());
            for (AnimationData.BoneAnimation boneAnimation : animation.getBoneAnimations()) {
                if (!this.modelParts.containsKey(boneAnimation.getName()))
                    continue;

                POSITION.set(0, 0, 0);
                ROTATION.set(0, 0, 0);
                SCALE.set(1, 1, 1);
                get(localAnimationTime, cache, cache.get(runtime, 0), 0, boneAnimation.getPositionFrames(), POSITION);
                get(localAnimationTime, cache, cache.get(runtime, 0), 0, boneAnimation.getRotationFrames(), ROTATION);
                get(localAnimationTime, cache, cache.get(runtime, 1), 1, boneAnimation.getScaleFrames(), SCALE);

                this.transformations.computeIfAbsent(boneAnimation.getName(), key -> new AnimatedModelPart.AnimationPose()).add(POSITION.x() * blendWeight, POSITION.y() * blendWeight, POSITION.z() * blendWeight, ROTATION.x() * blendWeight, ROTATION.y() * blendWeight, ROTATION.z() * blendWeight, (SCALE.x() - 1) * blendWeight, (SCALE.y() - 1) * blendWeight, (SCALE.z() - 1) * blendWeight);
            }
        }
        cache.clear();
        this.transformations.forEach((name, pose) ->
        {
            AnimatedModelPart.AnimationPose p = this.modelParts.get(name).getAnimationPose();
            p.reset();
            p.add(pose.getPosition().x(), pose.getPosition().y(), pose.getPosition().z(), pose.getRotation().x(), pose.getRotation().y(), pose.getRotation().z(), pose.getScale().x() - 1, pose.getScale().y() - 1, pose.getScale().z() - 1);
        });
    }

    @Override
    public GeometryModelData.Locator[] getLocators(String part) {
        return this.getModelPart(part).map(modelPart ->
        {
            if (!(modelPart instanceof AnimatedModelPart))
                return new GeometryModelData.Locator[0];
            return ((AnimatedModelPart) modelPart).getLocators();
        }).orElseGet(() -> new GeometryModelData.Locator[0]);
    }

    public String getActiveMaterial() {
        return activeMaterial;
    }

    private static void get(float animationTime, MolangCache cache, MolangEnvironment environment, float startValue, AnimationData.KeyFrame[] frames, Vector3f result) {
        if (frames.length == 1) {
            float x = cache.resolve(environment, frames[0].getTransformPostX());
            float y = cache.resolve(environment, frames[0].getTransformPostY());
            float z = cache.resolve(environment, frames[0].getTransformPostZ());
            result.set(x, y, z);
            return;
        }

        for (int i = 0; i < frames.length; i++) {
            AnimationData.KeyFrame to = frames[i];
            if ((to.getTime() < animationTime && i < frames.length - 1) || to.getTime() == 0)
                continue;

            AnimationData.KeyFrame from = i == 0 ? null : frames[i - 1];
            float progress = (from == null ? animationTime / to.getTime() : Math.min(1.0F, (animationTime - from.getTime()) / (to.getTime() - from.getTime())));
            if (to.getLerpMode() == AnimationData.LerpMode.CATMULLROM) {
                catmullRom(progress, cache, environment, startValue, i > 1 ? frames[i - 2] : null, from, to, i < frames.length - 1 ? frames[i + 1] : null, result);
            } else {
                lerp(to.getLerpMode().apply(progress), cache, environment, startValue, from, to, result);
            }
            break;
        }
    }

    private static void lerp(float progress, MolangCache cache, MolangEnvironment environment, float startValue, @Nullable AnimationData.KeyFrame from, AnimationData.KeyFrame to, Vector3f result) {
        float fromX = from == null ? startValue : cache.resolve(environment, from.getTransformPostX());
        float fromY = from == null ? startValue : cache.resolve(environment, from.getTransformPostY());
        float fromZ = from == null ? startValue : cache.resolve(environment, from.getTransformPostZ());

        float x = Mth.lerp(progress, fromX, cache.resolve(environment, to.getTransformPreX()));
        float y = Mth.lerp(progress, fromY, cache.resolve(environment, to.getTransformPreY()));
        float z = Mth.lerp(progress, fromZ, cache.resolve(environment, to.getTransformPreZ()));
        result.set(x, y, z);
    }

    private static void catmullRom(float progress, MolangCache cache, MolangEnvironment environment, float startValue, @Nullable AnimationData.KeyFrame before, @Nullable AnimationData.KeyFrame from, AnimationData.KeyFrame to, @Nullable AnimationData.KeyFrame after, Vector3f result) {
        float fromX = from == null ? startValue : cache.resolve(environment, from.getTransformPostX());
        float fromY = from == null ? startValue : cache.resolve(environment, from.getTransformPostY());
        float fromZ = from == null ? startValue : cache.resolve(environment, from.getTransformPostZ());

        float beforeX = before == null ? fromX : cache.resolve(environment, before.getTransformPostX());
        float beforeY = before == null ? fromY : cache.resolve(environment, before.getTransformPostY());
        float beforeZ = before == null ? fromZ : cache.resolve(environment, before.getTransformPostZ());

        float toX = cache.resolve(environment, to.getTransformPreX());
        float toY = cache.resolve(environment, to.getTransformPreY());
        float toZ = cache.resolve(environment, to.getTransformPreZ());

        float afterX = after == null ? toX : cache.resolve(environment, after.getTransformPreX());
        float afterY = after == null ? toY : cache.resolve(environment, after.getTransformPreY());
        float afterZ = after == null ? toZ : cache.resolve(environment, after.getTransformPreZ());

        result.set(catmullRom(beforeX, fromX, toX, afterX, progress), catmullRom(beforeY, fromY, toY, afterY, progress), catmullRom(beforeZ, fromZ, toZ, afterZ, progress));
    }

    private static float catmullRom(float p0, float p1, float p2, float p3, float t) {
        return 0.5F * ((2 * p1) + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);
    }

    private static float applyFrame(int count, FrameFunction terminator) throws MolangException {
        FrameTimer frameTimer = Minecraft.getInstance().getFrameTimer();
        long[] log = frameTimer.getLog();
        int duration = Math.min(count, log.length - 1); // Extended from 30 to 240 since that's what FrameTimer stores
        if (duration == 0)
            return (float) frameTimer.getLog()[frameTimer.getLogEnd()] / 1_000_000_000F; // ns to s
        if (duration < 0)
            throw new MolangException("Invalid argument for last_frame_time(): " + duration);
        int wrappedIndex = frameTimer.getLogEnd() - duration;
        while (wrappedIndex < 0)
            wrappedIndex += 240;

        int finalWrappedIndex = wrappedIndex;
        return (float) terminator.apply(LongStream.range(0, duration).map(i -> frameTimer.getLog()[frameTimer.wrapIndex((int) (finalWrappedIndex + i))])).orElse(0L) / 1_000_000_000F; // ns to s
    }

    @FunctionalInterface
    private interface FrameFunction {
        OptionalLong apply(LongStream stream) throws MolangException;
    }
}
