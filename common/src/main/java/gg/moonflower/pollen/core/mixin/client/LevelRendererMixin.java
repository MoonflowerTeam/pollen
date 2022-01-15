package gg.moonflower.pollen.core.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.Matrix4f;
import gg.moonflower.pollen.api.client.render.PollenDimensionSpecialEffects;
import gg.moonflower.pollen.core.client.render.PollenDimensionRenderContextImpl;
import gg.moonflower.pollen.core.extensions.LevelRendererExtension;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import gg.moonflower.pollen.pinwheel.core.client.DataContainerImpl;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.WeakHashMap;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements LevelRendererExtension {

    @Shadow
    private int ticks;

    @Shadow
    private ClientLevel level;
    @Shadow
    @Final
    private RenderBuffers renderBuffers;
    @Shadow
    @Final
    private Long2ObjectMap<SortedSet<BlockDestructionProgress>> destructionProgress;

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
    @Unique
    private final Map<ResourceKey<Level>, DataContainerImpl> dataContainer = new WeakHashMap<>(3);

    @Inject(method = "renderLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/LevelRenderer;globalBlockEntities:Ljava/util/Set;", shift = At.Shift.BEFORE, ordinal = 0))
    public void renderBlockRenders(PoseStack matrixStack, float partialTicks, long finishTimeNano, boolean drawBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmap, Matrix4f projection, CallbackInfo ci) {
        Vec3 vec3 = camera.getPosition();
        double x = vec3.x();
        double y = vec3.y();
        double z = vec3.z();

        this.pollen_getBlockRenderers().forEach(pos -> {
            BlockState state = this.level.getBlockState(pos);
            List<BlockRenderer> renderers = BlockRendererRegistry.get(state.getBlock());
            if (renderers.isEmpty())
                return;

            MultiBufferSource buffer = this.renderBuffers.bufferSource();
            matrixStack.pushPose();
            matrixStack.translate((double) pos.getX() - x, (double) pos.getY() - y, (double) pos.getZ() - z);
            SortedSet<BlockDestructionProgress> sortedSet = this.destructionProgress.get(pos.asLong());
            if (sortedSet != null && !sortedSet.isEmpty()) {
                int u = sortedSet.last().getProgress();
                if (u >= 0) {
                    PoseStack.Pose pose = matrixStack.last();
                    VertexConsumer vertexConsumer = new SheetedDecalTextureGenerator(this.renderBuffers.crumblingBufferSource().getBuffer(ModelBakery.DESTROY_TYPES.get(u)), pose.pose(), pose.normal());
                    buffer = renderType -> {
                        VertexConsumer vertexConsumer2 = this.renderBuffers.bufferSource().getBuffer(renderType);
                        return renderType.affectsCrumbling() ? VertexMultiConsumer.create(vertexConsumer, vertexConsumer2) : vertexConsumer2;
                    };
                }
            }

            for (BlockRenderer renderer : renderers) {
                matrixStack.pushPose();
                renderer.render(this.level, pos, this.pollen_getDataContainer(this.level, pos), buffer, matrixStack, partialTicks, camera, gameRenderer, lightmap, projection, LevelRenderer.getLightColor(this.level, pos), OverlayTexture.NO_OVERLAY);
                matrixStack.popPose();
            }
            matrixStack.popPose();
        });
    }

    @Inject(method = "setLevel", at = @At("HEAD"))
    public void setLevel(ClientLevel levelClient, CallbackInfo ci) {
        if (this.level != null)
            this.dataContainer.remove(this.level.dimension());
    }

    @Override
    public BlockRenderer.DataContainer pollen_getDataContainer(ClientLevel level, BlockPos pos) {
        return this.dataContainer.computeIfAbsent(level.dimension(), __ -> new DataContainerImpl(level)).get(pos);
    }

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
