package gg.moonflower.pollen.client.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import gg.moonflower.pollen.api.event.render.v1.RenderParticleEvents;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.TextureManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Shadow
    @Final
    private Map<ParticleRenderType, Queue<Particle>> particles;
    @Shadow
    @Final
    private TextureManager textureManager;
    @Unique
    private RenderParticleEvents.Context context;
    @Unique
    private final List<ParticleRenderType> renderOrder = new ArrayList<>();

    @Inject(method = "render", at = @At("HEAD"))
    public void renderPre(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LightTexture lightTexture, Camera camera, float partialTicks, CallbackInfo ci) {
        this.context = new RenderParticleEvents.Context() {
            @Override
            public ParticleEngine getParticleEngine() {
                return Minecraft.getInstance().particleEngine;
            }

            @Override
            public void addRenderType(ParticleRenderType type) {
                renderOrder.add(type);
            }
        };
        RenderParticleEvents.PRE.invoker().renderParticlesPre(this.context, bufferSource, lightTexture, camera, partialTicks);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void renderPost(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LightTexture lightTexture, Camera camera, float partialTicks, CallbackInfo ci) {
        lightTexture.turnOnLightLayer();
        RenderSystem.enableDepthTest();
        PoseStack poseStack2 = RenderSystem.getModelViewStack();
        poseStack2.pushPose();
        poseStack2.mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();

        for (ParticleRenderType particleRenderType : this.renderOrder) {
            Iterable<Particle> iterable = this.particles.get(particleRenderType);
            if (iterable != null) {
                RenderSystem.setShader(GameRenderer::getParticleShader);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferBuilder = tesselator.getBuilder();
                particleRenderType.begin(bufferBuilder, this.textureManager);

                for (Particle particle : iterable) {
                    try {
                        particle.render(bufferBuilder, camera, partialTicks);
                    } catch (Throwable var17) {
                        CrashReport crashReport = CrashReport.forThrowable(var17, "Rendering Particle");
                        CrashReportCategory crashReportCategory = crashReport.addCategory("Particle being rendered");
                        crashReportCategory.setDetail("Particle", particle::toString);
                        crashReportCategory.setDetail("Particle Type", particleRenderType::toString);
                        throw new ReportedException(crashReport);
                    }
                }

                particleRenderType.end(tesselator);
            }
        }

        poseStack2.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        lightTexture.turnOffLightLayer();

        RenderParticleEvents.POST.invoker().renderParticlesPost(this.context, bufferSource, lightTexture, camera, partialTicks);
        this.context = null;
        this.renderOrder.clear();
    }
}
