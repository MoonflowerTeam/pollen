package gg.moonflower.pollen.impl.pinwheel.render;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.api.pinwheel.v1.render.BlockRenderer;
import gg.moonflower.pollen.api.pinwheel.v1.render.BlockRendererRegistry;
import gg.moonflower.pollen.impl.pinwheel.blockdata.DataContainerImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@ApiStatus.Internal
public final class BlockRendererDispatcherImpl {

    private static final Map<BlockGetter, DataContainerImpl> DATA_CONTAINERS = new WeakHashMap<>(3);

    private BlockRendererDispatcherImpl() {
    }

    public static void render(LevelReader level, PoseStack matrixStack, MultiBufferSource buffer, Camera camera, List<BlockRenderer> renderers, BlockPos pos, int packedLight, int packedOverlay, float partialTicks) {
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        for (BlockRenderer renderer : renderers) {
            matrixStack.pushPose();
            renderer.render(level, pos, getDataContainer(level, pos), buffer, matrixStack, camera, gameRenderer, gameRenderer.lightTexture(), packedLight, packedOverlay, partialTicks);
            matrixStack.popPose();
        }
    }

    public static boolean shouldRender(BlockState state) {
        BlockRenderer renderer = BlockRendererRegistry.getFirst(state.getBlock());
        return renderer != null && renderer.getRenderShape(state) != RenderShape.MODEL;
    }

    public static BlockRenderer.DataContainer getDataContainer(BlockGetter level, BlockPos pos) {
        return DATA_CONTAINERS.computeIfAbsent(level, __ -> new DataContainerImpl(level)).get(pos);
    }
}
