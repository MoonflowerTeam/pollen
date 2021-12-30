package gg.moonflower.pollen.pinwheel.api.client.render;

import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Registers custom renderers for blocks.
 *
 * @see BlockRenderer
 * @since 1.0.0
 */
public class BlockRendererRegistry {

    private static final Map<Block, BlockRenderer> RENDERERS = new HashMap<>();

    /**
     * Registers a new renderer to the specified block.
     *
     * @param block    The block to bind a renderer to
     * @param renderer The renderer to use
     */
    public static synchronized void register(Block block, BlockRenderer renderer) {
        RENDERERS.put(block, renderer);
    }

    /**
     * Retrieves the renderer for the specified block.
     *
     * @param block The block to get the renderer for
     * @return The renderer for that block or <code>null</code> if one wasn't provided
     */
    @Nullable
    public static BlockRenderer get(Block block) {
        return RENDERERS.get(block);
    }
}
