package gg.moonflower.pollen.core.mixin.fabric.iris;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import gg.moonflower.pollen.core.extensions.LevelRendererExtension;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import net.coderbot.iris.mixin.LevelRendererAccessor;
import net.coderbot.iris.pipeline.ShadowRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ShadowRenderer.class)
public abstract class ShadowRendererMixin {

    @Shadow
    public static Matrix4f ORTHO;

    @Shadow
    private static ClientLevel getLevel() {
        throw new AssertionError();
    }

    @Unique
    private Camera captureCamera;

    @Inject(method = "renderShadows", at = @At("HEAD"))
    public void captureCamera(LevelRendererAccessor levelRenderer, Camera playerCamera, CallbackInfo ci) {
        this.captureCamera = playerCamera;
    }

    @Inject(method = "renderBlockEntities", at = @At("TAIL"), remap = false)
    public void renderBlockRenderers(MultiBufferSource.BufferSource buffer, PoseStack matrixStack, double cameraX, double cameraY, double cameraZ, float partialTicks, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        LevelRendererExtension extension = (LevelRendererExtension) minecraft.levelRenderer;
        GameRenderer gameRenderer = minecraft.gameRenderer;
        ClientLevel level = getLevel();

        extension.pollen_getBlockRenderers().forEach(pos -> {
            BlockState state = getLevel().getBlockState(pos);
            List<BlockRenderer> renderers = BlockRendererRegistry.get(state.getBlock());
            if (renderers.isEmpty())
                return;

            matrixStack.pushPose();
            matrixStack.translate((double) pos.getX() - cameraX, (double) pos.getY() - cameraY, (double) pos.getZ() - cameraZ);

            for (BlockRenderer renderer : renderers) {
                matrixStack.pushPose();
                renderer.render(level, pos, extension.pollen_getDataContainer(level, pos), buffer, matrixStack, partialTicks, this.captureCamera, gameRenderer, gameRenderer.lightTexture(), ORTHO, LevelRenderer.getLightColor(level, pos), OverlayTexture.NO_OVERLAY);
                matrixStack.popPose();
            }
            matrixStack.popPose();
        });

//        ((LevelRendererExtension) Minecraft.getInstance().levelRenderer).pollen_renderBlockRenderers(matrixStack, tickDelta, this.captureCamera, gameRenderer, gameRenderer.lightTexture(), ORTHO);
        this.captureCamera = null;
    }
}
