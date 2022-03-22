package gg.moonflower.pollen.pinwheel.api.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockData;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockDataKey;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

/**
 * Acts as a custom renderer for any block, instead of just tile entities.
 * <p>{@link RenderExclusively} should be used to define <i>how</i> this renderer should interact with other renderers of the same type
 *
 * @author Ocelot
 * @see RenderExclusively
 * @since 1.0.0
 */
public interface BlockRenderer {

    /**
     * Called after the renderer received an update from another renderer.
     *
     * @param level     The level the block is in
     * @param pos       The position of the block
     * @param oldState  The old state of the block
     * @param newState  The new state for the block
     * @param container The container for retrieving {@link BlockData}
     */
    default void receiveUpdate(Level level, BlockPos pos, BlockState oldState, BlockState newState, DataContainer container) {
    }

    /**
     * Called after normal block entity renders.
     *
     * @param level         The level the block is in
     * @param pos           The position of the block to render
     * @param container     The container for retrieving {@link BlockData}
     * @param buffer        The buffer for drawing into the level
     * @param matrixStack   The stack of matrix transformations for moving renders
     * @param partialTicks  The percentage from last tick to this tick
     * @param camera        The current camera perspective the game is being rendered from
     * @param gameRenderer  The renderer for the game
     * @param lightmap      The light map texture. Mainly used to disable the light map if desired
     * @param projection    The projection matrix of the scene
     * @param packedLight   The light of the block
     * @param packedOverlay The overlay coordinates to use on the render
     * @deprecated Use {@link #render(LevelReader, BlockPos, DataContainer, MultiBufferSource, PoseStack, Camera, GameRenderer, LightTexture, int, int, float)} instead. TODO remove in 2.0.0
     */
    @Deprecated
    void render(Level level, BlockPos pos, DataContainer container, MultiBufferSource buffer, PoseStack matrixStack, float partialTicks, Camera camera, GameRenderer gameRenderer, LightTexture lightmap, Matrix4f projection, int packedLight, int packedOverlay);

    /**
     * Called after normal block entity renders.
     *
     * @param level         The level the block is in
     * @param pos           The position of the block to render
     * @param container     The container for retrieving {@link BlockData}
     * @param buffer        The buffer for drawing into the level
     * @param matrixStack   The stack of matrix transformations for moving renders
     * @param camera        The current camera perspective the game is being rendered from
     * @param gameRenderer  The renderer for the game
     * @param lightmap      The light map texture. Mainly used to disable the light map if desired
     * @param packedLight   The light of the block
     * @param packedOverlay The overlay coordinates to use on the render
     * @param partialTicks  The percentage from last tick to this tick
     */
    @ApiStatus.OverrideOnly
    default void render(LevelReader level, BlockPos pos, DataContainer container, MultiBufferSource buffer, PoseStack matrixStack, Camera camera, GameRenderer gameRenderer, LightTexture lightmap, int packedLight, int packedOverlay, float partialTicks) {
        if (level instanceof Level)
            this.render((Level) level, pos, container, buffer, matrixStack, partialTicks, camera, gameRenderer, lightmap, null, packedLight, packedOverlay);
    }

    /**
     * Defines how the renderer will be applied to the specified block state.
     *
     * @param state The state to check
     * @return {@link RenderShape#INVISIBLE} will cause this renderer to apply, but not vanilla behavior<p>
     * {@link RenderShape#ENTITYBLOCK_ANIMATED} will cause this renderer to apply and vanilla behavior<p>
     * {@link RenderShape#MODEL} will cause vanilla behavior
     */
    @ApiStatus.OverrideOnly
    default RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    /**
     * Modifies the block state used to retrieve a model for rendering.
     * <p>Can be used to maintain a certain state and add custom rendering in place of a model. One example is animating the end portal frame eyes while retaining the frame.
     *
     * @param state The state to get the model for
     * @return A different state to get a model for
     */
    default BlockState getRenderState(BlockState state) {
        return state;
    }

    /**
     * Retrieves data by data keys. Also has the capability to send updates to neighboring renderers.
     *
     * @author Ocelot
     * @see BlockDataKey
     */
    interface DataContainer {

        /**
         * Updates the renderer at the specified position.
         *
         * @param direction The direction to update in
         */
        void updateNeighbor(Direction direction);

        /**
         * Fetches the block data for the specified key.
         *
         * @param key The key to retrieve the data for
         * @param <T> The type of data to retrieve
         * @return The data for the current block and key
         */
        <T> BlockData<T> get(BlockDataKey<T> key);

        /**
         * Fetches the block data at another position for the specified key.
         *
         * @param key The key to retrieve the data for
         * @param pos The position to fetch data for
         * @param <T> The type of data to retrieve
         * @return The data for the current block and key
         */
        <T> BlockData<T> get(BlockDataKey<T> key, BlockPos pos);
    }
}
