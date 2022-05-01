package gg.moonflower.pollen.pinwheel.api.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.pinwheel.core.client.DataContainerImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Properly draws block renderers, similar to {@link BlockEntityRenderDispatcher}.
 *
 * @author Ocelot
 * @since 1.1.0
 */
public final class BlockRendererDispatcher {

    private static final Map<BlockGetter, DataContainerImpl> DATA_CONTAINERS = new WeakHashMap<>(3);

    private BlockRendererDispatcher() {
    }

    /**
     * Renders the block renderers at the specified position for the specified state.
     *
     * @param level         The level the block is in
     * @param matrixStack   The current matrix transformation stack
     * @param buffer        The buffer to draw into
     * @param camera        The camera to draw relative to
     * @param state         The block state to draw
     * @param pos           The position of the block
     * @param packedLight   The light texture coordinates
     * @param packedOverlay The overlay texture coordinates
     * @param partialTicks  The percentage from last tick to this tick
     */
    public static void render(LevelReader level, PoseStack matrixStack, MultiBufferSource buffer, Camera camera, BlockState state, BlockPos pos, int packedLight, int packedOverlay, float partialTicks) {
        render(level, matrixStack, buffer, camera, BlockRendererRegistry.get(state.getBlock()), pos, packedLight, packedOverlay, partialTicks);
    }

    /**
     * Renders the block renderers at the specified position.
     *
     * @param level         The level the block is in
     * @param matrixStack   The current matrix transformation stack
     * @param buffer        The buffer to draw into
     * @param camera        The camera to draw relative to
     * @param renderers     The renderers to actually draw
     * @param pos           The position of the block
     * @param packedLight   The light texture coordinates
     * @param packedOverlay The overlay texture coordinates
     * @param partialTicks  The percentage from last tick to this tick
     */
    public static void render(LevelReader level, PoseStack matrixStack, MultiBufferSource buffer, Camera camera, List<BlockRenderer> renderers, BlockPos pos, int packedLight, int packedOverlay, float partialTicks) {
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        for (BlockRenderer renderer : renderers) {
            matrixStack.pushPose();
            renderer.render(level, pos, getDataContainer(level, pos), buffer, matrixStack, camera, gameRenderer, gameRenderer.lightTexture(), packedLight, packedOverlay, partialTicks);
            matrixStack.popPose();
        }
    }

    /**
     * Checks to see if the custom block renderer for the specified state should be rendered.
     *
     * @param state The state to check
     * @return If the {@link BlockRenderer} should render
     */
    public static boolean shouldRender(BlockState state) {
        BlockRenderer renderer = BlockRendererRegistry.getFirst(state.getBlock());
        return renderer != null && renderer.getRenderShape(state) != RenderShape.MODEL;
    }

    /**
     * Retrieves a data container at the specified position.
     *
     * @param level The level to get the container from
     * @param pos   The position of the container
     * @return A data container for that position. This can not be saved for later
     */
    public static BlockRenderer.DataContainer getDataContainer(BlockGetter level, BlockPos pos) {
        return DATA_CONTAINERS.computeIfAbsent(level, __ -> new DataContainerImpl(level)).get(pos);
    }
}
