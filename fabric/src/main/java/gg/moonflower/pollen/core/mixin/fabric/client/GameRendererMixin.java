package gg.moonflower.pollen.core.mixin.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import gg.moonflower.pollen.api.event.events.client.render.CameraSetupEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Unique
    private static final float[] CAMERA_STORAGE = new float[3];

    @Shadow
    public abstract Camera getMainCamera();

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", shift = At.Shift.AFTER))
    public void renderLevel(float partialTicks, long finishTimeNano, PoseStack matrixStack, CallbackInfo ci) {
        Camera mainCamera = this.getMainCamera();
        CAMERA_STORAGE[0] = mainCamera.getXRot();
        CAMERA_STORAGE[1] = mainCamera.getYRot();
        CAMERA_STORAGE[2] = 0.0F;

        CameraSetupEvent.EVENT.invoker().setupCamera((GameRenderer) (Object) this, mainCamera, new CameraSetupEvent.CameraSetter() {

            @Override
            public float getXRotation() {
                return CAMERA_STORAGE[0];
            }

            @Override
            public void setXRotation(float pitch) {
                CAMERA_STORAGE[0] = pitch;
            }

            @Override
            public float getYRotation() {
                return CAMERA_STORAGE[1];
            }

            @Override
            public void setYRotation(float yaw) {
                CAMERA_STORAGE[1] = yaw;
            }

            @Override
            public float getZRotation() {
                return CAMERA_STORAGE[2];
            }

            @Override
            public void setZRotation(float roll) {
                CAMERA_STORAGE[2] = roll;
            }
        }, partialTicks);

        ((CameraAccessor) mainCamera).setXRot(CAMERA_STORAGE[0]);
        ((CameraAccessor) mainCamera).setYRot(CAMERA_STORAGE[1]);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(CAMERA_STORAGE[2]));
    }
}
