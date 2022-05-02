package gg.moonflower.pollen.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.entity.PollinatedBoat;
import gg.moonflower.pollen.core.client.render.entity.PollinatedBoatRenderer;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("ConstantConditions")
@Mixin(BoatRenderer.class)
public class BoatRendererMixin {

    private Pair<ResourceLocation, BoatModel> pollen_capturedBoatResource;

    @Inject(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void capture(Boat boat, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo ci, float h, float j, float k, Pair<ResourceLocation, BoatModel> pair, ResourceLocation texture, BoatModel model) {
        if (!((Object) this instanceof PollinatedBoatRenderer))
            return;
        this.pollen_capturedBoatResource = ((PollinatedBoatRenderer) (Object) this).getBoatResources().get(((PollinatedBoat) boat).getBoatPollenType());
    }

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", index = 11, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", shift = At.Shift.AFTER))
    public ResourceLocation replaceTexture(ResourceLocation value) {
        if (!((Object) this instanceof PollinatedBoatRenderer))
            return value;
        return this.pollen_capturedBoatResource.getFirst();
    }

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/vehicle/Boat;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", index = 12, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V", shift = At.Shift.AFTER))
    public BoatModel replaceModel(BoatModel value) {
        if (!((Object) this instanceof PollinatedBoatRenderer))
            return value;
        return this.pollen_capturedBoatResource.getSecond();
    }
}
