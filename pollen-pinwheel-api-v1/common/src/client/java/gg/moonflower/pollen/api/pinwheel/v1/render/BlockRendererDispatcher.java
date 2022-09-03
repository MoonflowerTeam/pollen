package gg.moonflower.pollen.api.pinwheel.v1.render;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.impl.pinwheel.render.BlockRendererDispatcherImpl;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Properly draws block renderers, similar to {@link BlockEntityRenderDispatcher}.
 *
 * @author Ocelot
 * @since 1.1.0
 */
public interface BlockRendererDispatcher {

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
    static void render(LevelReader level, PoseStack matrixStack, MultiBufferSource buffer, Camera camera, BlockState state, BlockPos pos, int packedLight, int packedOverlay, float partialTicks) {
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
    static void render(LevelReader level, PoseStack matrixStack, MultiBufferSource buffer, Camera camera, List<BlockRenderer> renderers, BlockPos pos, int packedLight, int packedOverlay, float partialTicks) {
        BlockRendererDispatcherImpl.render(level, matrixStack, buffer, camera, renderers, pos, packedLight, packedOverlay, partialTicks);
    }

    /**
     * Checks to see if the custom block renderer for the specified state should be rendered.
     *
     * @param state The state to check
     * @return If the {@link BlockRenderer} should render
     */
    static boolean shouldRender(BlockState state) {
        return BlockRendererDispatcherImpl.shouldRender(state);
    }

    /**
     * Retrieves a data container at the specified position.
     *
     * @param level The level to get the container from
     * @param pos   The position of the container
     * @return A data container for that position. This can not be saved for later
     */
    static BlockRenderer.DataContainer getDataContainer(BlockGetter level, BlockPos pos) {
        return BlockRendererDispatcherImpl.getDataContainer(level, pos);
    }
}
