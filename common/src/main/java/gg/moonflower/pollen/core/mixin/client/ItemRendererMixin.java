package gg.moonflower.pollen.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.api.registry.client.ItemRendererRegistry;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @Shadow
    @Final
    private ItemModelShaper itemModelShaper;
    @Unique
    private Item captureItem;
    @Unique
    private ItemTransforms.TransformType captureTransform;

    @Inject(method = "getModel", at = @At("HEAD"))
    public void captureItem(ItemStack stack, Level level, LivingEntity livingEntity, int i, CallbackInfoReturnable<BakedModel> cir) {
        this.captureItem = stack.getItem();
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void captureTransform(ItemStack stack, ItemTransforms.TransformType transform, boolean leftHand, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay, BakedModel model, CallbackInfo ci) {
        this.captureItem = stack.getItem();
        this.captureTransform = transform;
    }

    @ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;getTransforms()Lnet/minecraft/client/renderer/block/model/ItemTransforms;", shift = At.Shift.BEFORE), index = 8, argsOnly = true)
    public BakedModel modifyModel(BakedModel value) {
        ModelResourceLocation modelLocation = ItemRendererRegistry.getModel(this.captureItem, this.captureTransform);
        if (modelLocation != null)
            return this.itemModelShaper.getModelManager().getModel(modelLocation);
        return value;
    }

    @ModifyVariable(method = "getModel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemModelShaper;getItemModel(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/client/resources/model/BakedModel;"), index = 5)
    public BakedModel modifyGuiModel(BakedModel value) {
        ModelResourceLocation modelLocation = ItemRendererRegistry.getModel(this.captureItem, ItemTransforms.TransformType.GUI);
        if (modelLocation != null)
            return this.itemModelShaper.getModelManager().getModel(modelLocation);
        return value;
    }
}
