package gg.moonflower.pollen.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.api.event.events.client.render.RenderParticleEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Shadow
    @Final
    private static List<ParticleRenderType> RENDER_ORDER;

    @Unique
    private RenderParticleEvents.Context context;
    @Unique
    private final List<ParticleRenderType> renderOrder = new ArrayList<>(RENDER_ORDER.size());

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;applyModelViewMatrix()V", ordinal = 0, shift = At.Shift.AFTER))
    public void renderPre(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LightTexture lightTexture, Camera camera, float partialTicks, CallbackInfo ci) {
        this.renderOrder.addAll(RENDER_ORDER);
        this.context = new RenderParticleEvents.Context() {
            @Override
            public ParticleEngine getParticleEngine() {
                return Minecraft.getInstance().particleEngine;
            }

            @Override
            public List<ParticleRenderType> getRenderOrder() {
                return renderOrder;
            }

            @Override
            public void cancel(ParticleRenderType type) {
                renderOrder.remove(type);
            }

            @Override
            public void addRenderTypeAfter(ParticleRenderType target, ParticleRenderType type) {
                renderOrder.remove(type); // Remove type if it was already there
                int index = renderOrder.indexOf(target);
                if (index == -1) {
                    renderOrder.add(type);
                } else {
                    renderOrder.add(index, type);
                }
            }
        };
        RenderParticleEvents.PRE.invoker().renderParticlesPre(this.context, bufferSource, lightTexture, camera, partialTicks);
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;applyModelViewMatrix()V", ordinal = 1, shift = At.Shift.BEFORE))
    public void renderPost(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LightTexture lightTexture, Camera camera, float partialTicks, CallbackInfo ci) {
        RenderParticleEvents.POST.invoker().renderParticlesPost(this.context, bufferSource, lightTexture, camera, partialTicks);
        this.context = null;
        this.renderOrder.clear();
    }

    // I would love to get rid of a redirect here, but I don't see another way of doing this
    @Redirect(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/particle/ParticleEngine;RENDER_ORDER:Ljava/util/List;"))
    public List<ParticleRenderType> modifyRenderOrder() {
        return renderOrder;
    }
}
