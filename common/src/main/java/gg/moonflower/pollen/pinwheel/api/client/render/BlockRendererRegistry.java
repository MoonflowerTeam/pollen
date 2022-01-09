package gg.moonflower.pollen.pinwheel.api.client.render;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Registers custom renderers for blocks and sorts them based on priority.
 *
 * @see BlockRenderer
 * @since 1.0.0
 */
public class BlockRendererRegistry {

    private static final RenderSet EMPTY = new RenderSet();
    private static final Map<Block, RenderSet> RENDERERS = new HashMap<>();
    private static final Lock LOCK = new ReentrantLock();

    /**
     * Registers a new renderer to the specified block.
     *
     * @param block    The block to bind a renderer to
     * @param renderer The renderer to use
     */
    public static void register(Block block, BlockRenderer renderer) {
        LOCK.lock();
        RenderSet renderers = RENDERERS.computeIfAbsent(block, __ -> new RenderSet());
        LOCK.unlock();

        RenderExclusively annotation = renderer.getClass().getDeclaredAnnotation(RenderExclusively.class);
        if (annotation != null && annotation.override()) {
            renderers.updateOverride(renderer, annotation);
        } else {
            renderers.addChild(renderer);
        }
    }

    /**
     * Retrieves the renderers in the order they should render for the specified block.
     *
     * @param block The block to get the renderers for
     * @return The renderers for that block
     */
    public static List<BlockRenderer> get(Block block) {
        return RENDERERS.getOrDefault(block, EMPTY).values();
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

    private static class RenderSet {

        private volatile BlockRenderer override;
        private final Set<BlockRenderer> children;
        private volatile List<BlockRenderer> cache;

        private RenderSet() {
            this.override = null;
            this.children = ConcurrentHashMap.newKeySet();
            this.cache = Collections.emptyList();
        }

        private synchronized void updateOverride(BlockRenderer renderer, RenderExclusively annotation) {
            if (this.override == null || annotation.priority() < getPriority(this.override)) {
                this.override = renderer;
                this.cache = null;
            }
        }

        private void addChild(BlockRenderer renderer) {
            this.children.add(renderer);
            this.cache = null;
        }

        private List<BlockRenderer> values() {
            if (this.cache == null) {
                this.cache = new ArrayList<>(this.children);
                this.cache.sort(Comparator.comparingInt(RenderSet::getPriority)); // Sort by priority
                if (this.override != null)
                    this.cache.add(0, this.override); // Add override first
            }
            return this.cache;
        }

        private static int getPriority(BlockRenderer renderer) {
            RenderExclusively annotation = renderer.getClass().getDeclaredAnnotation(RenderExclusively.class);
            return annotation != null ? annotation.priority() : 1000;
        }
    }
}
