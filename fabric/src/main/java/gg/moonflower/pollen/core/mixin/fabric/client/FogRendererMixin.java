package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.event.events.client.render.FogEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Shadow
    private static float fogRed;

    @Shadow
    private static float fogGreen;

    @Shadow
    private static float fogBlue;

    @Inject(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V", shift = At.Shift.BEFORE))
    private static void modifyFogColor(Camera camera, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        FogEvents.FOG_COLOR.invoker().setupFogColors(Minecraft.getInstance().gameRenderer, camera, new FogEvents.ColorContext() {
            @Override
            public float getRed() {
                return fogRed;
            }

            @Override
            public float getGreen() {
                return fogGreen;
            }

            @Override
            public float getBlue() {
                return fogBlue;
            }

            @Override
            public void setRed(float red) {
                fogRed = red;
            }

            @Override
            public void setGreen(float green) {
                fogGreen = green;
            }

            @Override
            public void setBlue(float blue) {
                fogBlue = blue;
            }
        }, partialTicks);
    }

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void modifyFogDensity(Camera camera, FogRenderer.FogMode fogType, float farPlaneDistance, boolean nearFog, float partialTicks, CallbackInfo ci) {
        FogEvents.FOG_DENSITY.invoker().setupFogDensity(Minecraft.getInstance().gameRenderer, camera, farPlaneDistance, partialTicks);
    }
}
