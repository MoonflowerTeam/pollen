package gg.moonflower.pollen.core.mixin.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.api.entity.PollenEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow
    protected M model;

    @Unique
    private T captureEntity;
    @Unique
    private boolean shouldCancelRide;
    @Unique
    private float capturePartialTicks;
    @Unique
    private float captureJ;
    @Unique
    private float captureH;

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/model/EntityModel;riding:Z", shift = At.Shift.AFTER))
    public void modifyRiding(T entity, float f, float capturePartialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        this.captureEntity = entity;
        this.capturePartialTicks = capturePartialTicks;
        this.shouldCancelRide = entity.getVehicle() instanceof PollenEntity pollenEntity && !pollenEntity.shouldRiderSit();
        if (this.model.riding && this.shouldCancelRide)
            this.model.riding = false;
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("TAIL"))
    public void clearCaptured(T livingEntity, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci) {
        this.captureEntity = null;
    }

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getBob(Lnet/minecraft/world/entity/LivingEntity;F)F"), ordinal = 2)
    public float modifyH(float value) {
        if (this.shouldCancelRide)
            return this.captureH = Mth.rotLerp(this.capturePartialTicks, this.captureEntity.yBodyRotO, this.captureEntity.yBodyRot);
        return value;
    }

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getBob(Lnet/minecraft/world/entity/LivingEntity;F)F"), ordinal = 3)
    public float modifyJ(float value) {
        if (this.shouldCancelRide)
            return this.captureJ = Mth.rotLerp(this.capturePartialTicks, this.captureEntity.yHeadRotO, this.captureEntity.yHeadRot);
        return value;
    }

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getBob(Lnet/minecraft/world/entity/LivingEntity;F)F"), ordinal = 4)
    public float modifyK(float value) {
        if (this.shouldCancelRide)
            return (this.captureJ - this.captureH) * (LivingEntityRenderer.isEntityUpsideDown(this.captureEntity) ? -1 : 1);
        return value;
    }

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;prepareMobModel(Lnet/minecraft/world/entity/Entity;FFF)V", shift = At.Shift.BEFORE), ordinal = 7)
    public float modifyN(float value) {
        return this.shouldCancelRide ? 0 : value;
    }

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;prepareMobModel(Lnet/minecraft/world/entity/Entity;FFF)V", shift = At.Shift.BEFORE), ordinal = 8)
    public float modifyO(float value) {
        return this.shouldCancelRide ? 0 : value;
    }
}
