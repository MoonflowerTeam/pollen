package gg.moonflower.pollen.core.mixin.fabric.client;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import gg.moonflower.pollen.api.event.events.client.render.CameraSetupEvent;
import gg.moonflower.pollen.api.registry.client.ShaderRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

    @Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;shutdownShaders()V", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    public void reloadShaders(ResourceManager resourceManager, CallbackInfo ci, List<Program> list, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> list1) {
        try {
            for (Map.Entry<ResourceLocation, VertexFormat> entry : ShaderRegistry.getRegisteredShaders()) {
                list1.add(Pair.of(new ShaderInstance(resourceManager, entry.getKey().toString(), entry.getValue()), instance -> ShaderRegistry.loadShader(entry.getKey(), instance)));
            }
        } catch (IOException e) {
            list1.forEach(pair -> pair.getFirst().close());
            throw new RuntimeException("could not reload shaders", e);
        }
    }
}
