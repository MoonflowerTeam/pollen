package gg.moonflower.pollen.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import gg.moonflower.pollen.api.client.render.PollenDimensionSpecialEffects;
import gg.moonflower.pollen.core.client.render.PollenDimensionRenderContextImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    private int ticks;
    @Shadow
    private ClientLevel level;

    @Unique
    private PoseStack captureMatrixStack;
    @Unique
    private float capturePartialTicks;
    @Unique
    private Camera captureCamera;
    @Unique
    private Matrix4f captureProjection;
    @Unique
    private final PollenDimensionSpecialEffects.RenderContext renderContext = new PollenDimensionRenderContextImpl(() -> this.ticks, () -> this.capturePartialTicks, () -> this.captureCamera, () -> this.level, () -> this.captureMatrixStack, () -> this.captureProjection);

    @Inject(method = "renderLevel", at = @At("HEAD"))
    public void renderLevel(PoseStack matrixStack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmap, Matrix4f projection, CallbackInfo ci) {
        this.captureMatrixStack = matrixStack;
        this.capturePartialTicks = partialTicks;
        this.captureCamera = camera;
        this.captureProjection = projection;
    }

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    public void renderClouds(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, double camX, double camY, double camZ, CallbackInfo ci) {
        DimensionSpecialEffects specialEffects = this.level.effects();
        if (specialEffects instanceof PollenDimensionSpecialEffects) {
            PollenDimensionSpecialEffects.Renderer renderer = ((PollenDimensionSpecialEffects) specialEffects).getCloudRenderer();
            if (renderer != null) {
                renderer.render(this.renderContext);
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderSnowAndRain", at = @At("HEAD"), cancellable = true)
    public void renderSnowAndRain(LightTexture lightmap, float partialTicks, double x, double y, double z, CallbackInfo ci) {
        DimensionSpecialEffects specialEffects = this.level.effects();
        if (specialEffects instanceof PollenDimensionSpecialEffects) {
            PollenDimensionSpecialEffects.Renderer renderer = ((PollenDimensionSpecialEffects) specialEffects).getWeatherRenderer();
            if (renderer != null) {
                renderer.render(this.renderContext);
                ci.cancel();
            }
        }
    }

    @Inject(method = "tickRain", at = @At("HEAD"), cancellable = true)
    public void tickRain(Camera camera, CallbackInfo ci) {
        DimensionSpecialEffects specialEffects = this.level.effects();
        if (specialEffects instanceof PollenDimensionSpecialEffects) {
            PollenDimensionSpecialEffects.Renderer renderer = ((PollenDimensionSpecialEffects) specialEffects).getWeatherParticleRenderer();
            if (renderer != null) {
                renderer.render(this.renderContext);
                ci.cancel();
            }
        }
    }

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    public void renderSky(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick, Runnable skyFogSetup, CallbackInfo ci) {
        DimensionSpecialEffects specialEffects = this.level.effects();
        if (specialEffects instanceof PollenDimensionSpecialEffects) {
            PollenDimensionSpecialEffects.Renderer renderer = ((PollenDimensionSpecialEffects) specialEffects).getSkyRenderer();
            if (renderer != null) {
                renderer.render(this.renderContext);
                ci.cancel();
            }
        }
    }
}
