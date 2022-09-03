package gg.moonflower.pollen.api.pinwheel.v1.render;

import gg.moonflower.pollen.impl.pinwheel.render.BlockRendererRegistryImpl;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Registers custom renderers for blocks and sorts them based on priority.
 *
 * @see BlockRenderer
 * @since 1.0.0
 */
public class BlockRendererRegistry {

    /**
     * Registers a new renderer to the specified block.
     *
     * @param block    The block to bind a renderer to
     * @param renderer The renderer to use
     */
    public static void register(Block block, BlockRenderer renderer) {
        BlockRendererRegistryImpl.register(block, renderer);
    }

    /**
     * Registers a new renderer to all blocks in the specified tag.
     *
     * @param tag      The tag to get blocks from to bind a renderer to
     * @param renderer The renderer to use
     */
    public static void register(TagKey<Block> tag, BlockRenderer renderer) {
        BlockRendererRegistryImpl.register(tag, renderer);
    }

    /**
     * Retrieves the renderers in the order they should render for the specified block.
     *
     * @param block The block to get the renderers for
     * @return The renderers for that block
     */
    public static List<BlockRenderer> get(Block block) {
        return BlockRendererRegistryImpl.get(block);
    }

    /**
     * Retrieves the first renderer for the specified block.
     *
     * @param block The block to get the renderer for
     * @return The renderer for that block or <code>null</code> for no renderers
     */
    @Nullable
    public static BlockRenderer getFirst(Block block) {
        List<BlockRenderer> renderers = get(block);
        return !renderers.isEmpty() ? renderers.get(0) : null;
    }
}
