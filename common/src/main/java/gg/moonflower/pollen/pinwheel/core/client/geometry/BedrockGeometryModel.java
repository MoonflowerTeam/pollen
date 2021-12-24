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
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class BedrockGeometryModel extends Model implements GeometryModel, AnimatedModel {

    private static final Vector3f POSITION = new Vector3f();
    private static final Vector3f ROTATION = new Vector3f();
    private static final Vector3f SCALE = new Vector3f();

    private final Map<String, AnimatedModelPart.AnimationPose> transformations;
    private final Map<String, BoneModelPart> modelParts;
    private final Set<BoneModelPart> renderParts;
    private final String[] modelKeys;
    private final String[] textureKeys;
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

                    ModelPart parentRenderer = boneLookup.get(parent).getRight();
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

    private static void get(float animationTime, MolangRuntime.Builder runtime, AnimationData.KeyFrame[] frames, Vector3f result) {
        if (frames.length == 1) {
            // TODO figure out what "this" is supposed to be
            float x = frames[0].getTransformPostX().safeResolve(runtime.create(0));
            float y = frames[0].getTransformPostY().safeResolve(runtime.create(0));
            float z = frames[0].getTransformPostZ().safeResolve(runtime.create(0));
            result.set(x, y, z);
            return;
        }

        for (int i = 0; i < frames.length; i++) {
            AnimationData.KeyFrame to = frames[i];
            if ((to.getTime() < animationTime && i < frames.length - 1) || to.getTime() == 0)
                continue;

            AnimationData.KeyFrame from = i == 0 ? null : frames[i - 1];
            float progress = (from == null ? animationTime / to.getTime() : Math.min(1.0F, (animationTime - from.getTime()) / (to.getTime() - from.getTime())));
            switch (to.getLerpMode()) {
                case LINEAR:
                    lerp(progress, runtime, from, to, result);
                    break;
                case CATMULLROM:
                    catmullRom(progress, runtime, i > 1 ? frames[i - 2] : null, from, to, i < frames.length - 1 ? frames[i + 1] : null, result);
                    break;
            }
            break;
        }
    }

    private static void lerp(float progress, MolangRuntime.Builder runtime, @Nullable AnimationData.KeyFrame from, AnimationData.KeyFrame to, Vector3f result) {
        float fromX = from == null ? 0 : from.getTransformPostX().safeResolve(runtime.create(0));
        float fromY = from == null ? 0 : from.getTransformPostY().safeResolve(runtime.create(0));
        float fromZ = from == null ? 0 : from.getTransformPostZ().safeResolve(runtime.create(0));

        float x = Mth.lerp(progress, fromX, to.getTransformPreX().safeResolve(runtime.create(0)));
        float y = Mth.lerp(progress, fromY, to.getTransformPreY().safeResolve(runtime.create(0)));
        float z = Mth.lerp(progress, fromZ, to.getTransformPreZ().safeResolve(runtime.create(0)));
        result.set(x, y, z);
    }

    private static void catmullRom(float progress, MolangRuntime.Builder runtime, @Nullable AnimationData.KeyFrame before, @Nullable AnimationData.KeyFrame from, AnimationData.KeyFrame to, @Nullable AnimationData.KeyFrame after, Vector3f result) {
        float fromX = from == null ? 0 : from.getTransformPostX().safeResolve(runtime.create(0));
        float fromY = from == null ? 0 : from.getTransformPostY().safeResolve(runtime.create(0));
        float fromZ = from == null ? 0 : from.getTransformPostZ().safeResolve(runtime.create(0));

        float beforeX = before == null ? fromX : before.getTransformPostX().safeResolve(runtime.create(0));
        float beforeY = before == null ? fromY : before.getTransformPostY().safeResolve(runtime.create(0));
        float beforeZ = before == null ? fromZ : before.getTransformPostZ().safeResolve(runtime.create(0));

        float toX = to.getTransformPreX().safeResolve(runtime.create(0));
        float toY = to.getTransformPreY().safeResolve(runtime.create(0));
        float toZ = to.getTransformPreZ().safeResolve(runtime.create(0));

        float afterX = after == null ? toX : after.getTransformPreX().safeResolve(runtime.create(0));
        float afterY = after == null ? toY : after.getTransformPreY().safeResolve(runtime.create(0));
        float afterZ = after == null ? toZ : after.getTransformPreZ().safeResolve(runtime.create(0));

        result.set(catmullRom(beforeX, fromX, toX, afterX, progress), catmullRom(beforeY, fromY, toY, afterY, progress), catmullRom(beforeZ, fromZ, toZ, afterZ, progress));
    }

    private static float catmullRom(float p0, float p1, float p2, float p3, float t) {
        return 0.5F * ((2 * p1) + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);
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
    public void applyAnimations(float animationTime, MolangRuntime.Builder runtime, AnimationData... animations) {
        if (animations.length == 0)
            return;

        runtime.setQuery("delta_time", Minecraft.getInstance()::getFrameTime);
        runtime.setQuery("life_time", animationTime);

        animationTime %= AnimatedModel.getAnimationLength(animationTime, animations);

        this.transformations.values().forEach(AnimatedModelPart.AnimationPose::reset);
        for (AnimationData animation : animations) {
            float localAnimationTime = animationTime;
            if (localAnimationTime > animation.getAnimationLength()) {
                localAnimationTime = animation.getAnimationLength();
            }

            float blendWeight = animation.getBlendWeight();
            for (AnimationData.BoneAnimation boneAnimation : animation.getBoneAnimations()) {
                if (!this.modelParts.containsKey(boneAnimation.getName()) || animation.getBoneAnimations().length == 0)
                    continue;

                POSITION.set(0, 0, 0);
                ROTATION.set(0, 0, 0);
                SCALE.set(1, 1, 1);
                get(localAnimationTime, runtime, boneAnimation.getPositionFrames(), POSITION);
                get(localAnimationTime, runtime, boneAnimation.getRotationFrames(), ROTATION);
                get(localAnimationTime, runtime, boneAnimation.getScaleFrames(), SCALE);

                this.transformations.computeIfAbsent(boneAnimation.getName(), key -> new AnimatedModelPart.AnimationPose()).add(POSITION.x() * blendWeight, POSITION.y() * blendWeight, POSITION.z() * blendWeight, ROTATION.x() * blendWeight, ROTATION.y() * blendWeight, ROTATION.z() * blendWeight, (SCALE.x() - 1) * blendWeight, (SCALE.y() - 1) * blendWeight, (SCALE.z() - 1) * blendWeight);
            }
        }
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
}
