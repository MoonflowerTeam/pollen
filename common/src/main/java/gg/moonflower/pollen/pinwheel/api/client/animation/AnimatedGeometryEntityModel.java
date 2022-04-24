package gg.moonflower.pollen.pinwheel.api.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModel;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelRenderer;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationData;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import io.github.ocelot.molangcompiler.api.bridge.MolangVariableProvider;
import io.github.ocelot.molangcompiler.api.exception.MolangException;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * A basic implementation of {@link EntityModel} for {@link GeometryModel}.
 *
 * @param <T> The type of entity this model is rendering
 * @author Ocelot
 * @since 1.0.0
 */
public class AnimatedGeometryEntityModel<T extends Entity> extends EntityModel<T> {

    private final ResourceLocation model;
    private ResourceLocation texture;
    private ResourceLocation[] animations;
    private float[] animationWeights;
    private MolangVariableProvider variableProvider;

    public AnimatedGeometryEntityModel(ResourceLocation model) {
        this.model = model;
        this.texture = null;
        this.animations = new ResourceLocation[0];
        this.animationWeights = new float[0];
        this.variableProvider = null;
    }

    private MolangRuntime.Builder createRuntime(T entity, float limbSwing, float limbSwingAmount, float yaw, float pitch) {
        float partialTicks = Minecraft.getInstance().getFrameTime();
        MolangRuntime.Builder builder = MolangRuntime.runtime();

        // Level
        if (entity.level instanceof ClientLevel)
            builder.setQuery("actor_count", () -> (float) ((ClientLevel) entity.level).getEntityCount());
        builder.setQuery("time_of_day", () -> entity.level.getTimeOfDay(partialTicks) / 24_000L);
        builder.setQuery("day", () -> (float) (entity.level.getDayTime() / 24000L + 1));
        builder.setQuery("moon_phase", () -> (float) entity.level.getMoonPhase());
        builder.setQuery("moon_brightness", () -> entity.level.getMoonBrightness());

        // Basic queries
        builder.setQuery("is_on_ground", entity.isOnGround() ? 1.0F : 0.0F);
        builder.setQuery("is_in_water", entity.isInWater() ? 1.0F : 0.0F);
        builder.setQuery("is_in_water_or_rain", entity.isInWaterOrRain() ? 1.0F : 0.0F);
        builder.setQuery("is_in_contact_with_water", entity.isInWaterRainOrBubble() ? 1.0F : 0.0F);
        builder.setQuery("is_moving", () -> entity.getDeltaMovement().lengthSqr() > 1.0E-7D ? 1.0F : 0.0F); // requires *some* calculation, so make it lazy
        builder.setQuery("is_alive", entity.isAlive() ? 1.0F : 0.0F);
        builder.setQuery("is_fire_immune", entity.fireImmune() ? 1.0F : 0.0F);
        builder.setQuery("is_on_fire", entity.isOnFire() ? 1.0F : 0.0F);
        builder.setQuery("is_first_person", entity == Minecraft.getInstance().getCameraEntity() && Minecraft.getInstance().options.getCameraType() == CameraType.FIRST_PERSON ? 1.0F : 0.0F);
        builder.setQuery("is_invisible", entity.isInvisible() ? 1.0F : 0.0F);
        builder.setQuery("is_ghost", entity.isSpectator() ? 1.0F : 0.0F);

        // Speed
        builder.setQuery("ground_speed", () -> {
            Vec3 velocity = entity.getDeltaMovement();
            return Mth.sqrt((float) ((velocity.x * velocity.x) + (velocity.z * velocity.z)));
        });
        if (entity instanceof LivingEntity)
            builder.setQuery("modified_move_speed", ((LivingEntity) entity).getSpeed());
        builder.setQuery("modified_distance_moved", entity.moveDist);
        builder.setQuery("vertical_speed", (float) entity.getDeltaMovement().y());

        // Rotation
        builder.setQuery("head_x_rotation", pitch);
        builder.setQuery("head_y_rotation", yaw);
        if (entity instanceof LivingEntity)
            builder.setQuery("body_y_rotation", () -> Mth.lerp(partialTicks, ((LivingEntity) entity).yBodyRotO, ((LivingEntity) entity).yBodyRot));
        builder.setQuery("head_x_rotation_speed", () -> entity.getViewXRot(partialTicks) - entity.getViewXRot((float) (partialTicks - 0.1)));
        builder.setQuery("head_y_rotation_speed", () -> entity.getViewYRot(partialTicks) - entity.getViewYRot((float) (partialTicks - 0.1)));

        // Living specific properties
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            builder.setQuery("health", livingEntity.getHealth());
            builder.setQuery("max_health", livingEntity.getMaxHealth());
            builder.setQuery("is_baby", livingEntity.isBaby() ? 1.0F : 0.0F);
        }

        // Misc math
        builder.setQuery("distance_from_camera", () -> (float) Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(entity.position()));
        builder.setQuery("camera_distance_range_lerp", 2, context ->
        {
            float first = context.resolve(0);
            float second = context.resolve(1);
            if (first == second)
                return 1.0F;

            float smaller = Math.min(first, second);
            float larger = Math.max(first, second);

            double distance = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(entity.position());
            if (distance <= smaller)
                return smaller;
            if (distance >= larger)
                return larger;
            return (float) ((distance - smaller) / (larger - smaller));
        });
        builder.setQuery("lod_index", -1, context ->
        {
            if (context.getParameters() <= 0)
                return 0;

            double distance = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().distanceTo(entity.position());
            for (int i = 0; i < context.getParameters(); i++)
                if (distance < context.resolve(0))
                    return i;
            return context.getParameters() - 1;
        });
        builder.setQuery("position", 1, context ->
        {
            int index = (int) context.resolve(0);
            if (index < 0 || index >= 3)
                throw new MolangException("Invalid argument for position(): " + index);
            return (float) (index == 0 ? entity.getX(partialTicks) : index == 1 ? entity.getY(partialTicks) : entity.getZ(partialTicks));
        });
        builder.setQuery("position_delta", 1, context ->
        {
            int index = (int) context.resolve(0);
            if (index < 0 || index >= 3)
                throw new MolangException("Invalid argument for position(): " + index);
            return (float) (index == 0 ? entity.getDeltaMovement().x() : index == 1 ? entity.getDeltaMovement().y() : entity.getDeltaMovement().z());
        });

        // Custom Queries
        builder.setQuery("limb_swing", limbSwing);
        builder.setQuery("limb_swing_amount", limbSwingAmount);

        return builder;
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float animationTicks, float netHeadYaw, float headPitch) {
        GeometryModel model = this.getModel();
        model.resetTransformation();
        if (model instanceof AnimatedModel && this.animations.length > 0) {
            AnimationData[] animationData = this.getAnimations();
            if (animationData.length == 0)
                return;

            ProfilerFiller profiler = Minecraft.getInstance().getProfiler();
            profiler.push("createMolangRuntime");
            MolangRuntime.Builder builder = this.createRuntime(entity, limbSwing, limbSwingAmount, netHeadYaw, headPitch);
            if (entity instanceof MolangVariableProvider)
                builder.setVariables((MolangVariableProvider) entity);
            if (this.variableProvider != null)
                builder.setVariables(this.variableProvider);
            profiler.popPush("applyMolangAnimation");
            ((AnimatedModel) model).applyAnimations(animationTicks / 20F, builder, this.animationWeights, animationData);
            profiler.pop();
        }
    }

    @Override
    public void renderToBuffer(PoseStack matrixStack, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.renderToBuffer(matrixStack, Minecraft.getInstance().renderBuffers().bufferSource(), packedLight, packedOverlay, red, green, blue, alpha);
    }

    public void renderToBuffer(PoseStack matrixStack, MultiBufferSource source, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        matrixStack.pushPose();
        matrixStack.translate(0, 1.501F, 0); // LivingEntityRenderer translates after scaling, so we have to undo that
        GeometryModelRenderer.render(this.getModel(), this.texture, source, matrixStack, packedLight, packedOverlay, red, green, blue, alpha);
        matrixStack.popPose();
    }

    /**
     * @return The model this model is wrapping
     */
    public GeometryModel getModel() {
        return GeometryModelManager.getModel(this.model);
    }

    /**
     * @return The name of the texture table to render with
     */
    @Nullable
    public ResourceLocation getTexture() {
        return texture;
    }

    /**
     * Sets the texture table to render with.
     *
     * @param texture The new texture
     */
    public void setTexture(@Nullable ResourceLocation texture) {
        this.texture = texture;
    }

    /**
     * @return The names of the animations this entity model is playing
     */
    public ResourceLocation[] getAnimationNames() {
        return this.animations;
    }

    /**
     * @return The animations this entity model is playing
     */
    public AnimationData[] getAnimations() {
        return Arrays.stream(this.animations).map(AnimationManager::getAnimation).filter(animation -> animation != AnimationData.EMPTY).toArray(AnimationData[]::new);
    }

    /**
     * Sets the new animation to use.
     *
     * @param animations The animations to play
     */
    public void setAnimations(ResourceLocation... animations) {
        this.animations = animations;
    }

    /**
     * Sets the weights to use for animation.
     *
     * @param animationWeights The new weights
     */
    public void setAnimationWeights(float[] animationWeights) {
        this.animationWeights = animationWeights;
    }

    /**
     * Sets an additional provider for MoLang variables.
     *
     * @param variableProvider The provider for variables in addition to the entity's variable provider
     */
    public void setVariableProvider(@Nullable MolangVariableProvider variableProvider) {
        this.variableProvider = variableProvider;
    }
}
