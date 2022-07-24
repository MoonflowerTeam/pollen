package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public class FogRendererMixin {

    @Shadow
    private static int targetBiomeFog;
    @Shadow
    private static int previousBiomeFog;
    @Shadow
    private static float fogRed;
    @Shadow
    private static float fogGreen;
    @Shadow
    private static float fogBlue;

    @Shadow
    private static long biomeChangedTime;
    @Unique
    private static float capturePartialTicks;
    @Unique
    private static Fluid changeFluid;
    @Unique
    private static long customFluidBiomeChangeTime;

    @Inject(method = "setupColor", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel$ClientLevelData;getClearColorScale()F", shift = At.Shift.BEFORE))
    private static void setupColor(Camera camera, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
        capturePartialTicks = partialTicks;

        Fluid fluid = level.getFluidState(camera.getBlockPosition()).getType();
        if (fluid instanceof PollinatedFluid) {
            biomeChangedTime = -1;

            long l = Util.getMillis();
            int i = ((PollinatedFluid) fluid).getFogColor(camera, (ClientLevel) camera.getEntity().level, camera.getEntity().level.getBiome(camera.getBlockPosition()), partialTicks);
            if (changeFluid != fluid || customFluidBiomeChangeTime < 0L) {
                changeFluid = fluid;
                targetBiomeFog = i;
                previousBiomeFog = i;
                customFluidBiomeChangeTime = l;
            }

            int j = targetBiomeFog >> 16 & 0xFF;
            int k = targetBiomeFog >> 8 & 0xFF;
            int m = targetBiomeFog & 0xFF;
            int n = previousBiomeFog >> 16 & 0xFF;
            int o = previousBiomeFog >> 8 & 0xFF;
            int p = previousBiomeFog & 0xFF;
            float f = Mth.clamp((float) (l - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
            float g = Mth.lerp(f, (float) n, (float) j);
            float h = Mth.lerp(f, (float) o, (float) k);
            float q = Mth.lerp(f, (float) p, (float) m);
            fogRed = g / 255.0F;
            fogGreen = h / 255.0F;
            fogBlue = q / 255.0F;
            if (targetBiomeFog != i) {
                targetBiomeFog = i;
                previousBiomeFog = Mth.floor(g) << 16 | Mth.floor(h) << 8 | Mth.floor(q);
                customFluidBiomeChangeTime = l;
            }
        } else {
            changeFluid = null;
        }
    }

    @Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
    private static void setupFog(Camera camera, FogRenderer.FogMode fogType, float farPlaneDistance, boolean nearFog, float g, CallbackInfo ci) {
        Fluid fluid = camera.getEntity().level.getFluidState(camera.getBlockPosition()).getType();
        if (fluid instanceof PollinatedFluid) {
            ((PollinatedFluid) fluid).applyFog(Minecraft.getInstance().gameRenderer, camera, farPlaneDistance, capturePartialTicks);
            ci.cancel();
        }
    }
}
