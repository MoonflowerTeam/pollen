package gg.moonflower.pollen.impl.pinwheel.render;

import gg.moonflower.pollen.api.event.events.client.resource.ClientTagUpdateEvent;
import gg.moonflower.pollen.api.pinwheel.v1.render.BlockRenderer;
import gg.moonflower.pollen.api.pinwheel.v1.render.RenderExclusively;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ApiStatus.Internal
public class BlockRendererRegistryImpl {

    private static final RenderSet EMPTY = new RenderSet();
    private static final Map<Block, RenderSet> RENDERERS = new HashMap<>();
    private static final Map<TagKey<Block>, RenderSet> TAG_RENDERERS = new HashMap<>();
    private static final Lock RENDERERS_LOCK = new ReentrantLock();
    private static final Lock TAG_RENDERERS_LOCK = new ReentrantLock();
    private static final Map<Block, RenderSet> RENDERERS_CACHE = new ConcurrentHashMap<>();

    static {
        ClientTagUpdateEvent.EVENT.register(RENDERERS_CACHE::clear);
    }

    private static void addToSet(RenderSet renderers, BlockRenderer renderer) {
        RenderExclusively annotation = renderer.getClass().getDeclaredAnnotation(RenderExclusively.class);
        if (annotation != null && annotation.override()) {
            renderers.updateOverride(renderer, annotation);
        } else {
            renderers.addChild(renderer);
        }
    }

    public static void register(Block block, BlockRenderer renderer) {
        RENDERERS_LOCK.lock();
        RenderSet renderers = RENDERERS.computeIfAbsent(block, __ -> new RenderSet());
        RENDERERS_LOCK.unlock();
        addToSet(renderers, renderer);
    }

    public static void register(TagKey<Block> tag, BlockRenderer renderer) {
        TAG_RENDERERS_LOCK.lock();
        RenderSet renderers = TAG_RENDERERS.computeIfAbsent(tag, __ -> new RenderSet());
        TAG_RENDERERS_LOCK.unlock();
        addToSet(renderers, renderer);
    }

    public static List<BlockRenderer> get(Block block) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) {
            RENDERERS_LOCK.lock();
            try {
                return RENDERERS.getOrDefault(block, EMPTY).values();
            } finally {
                RENDERERS_LOCK.unlock();
            }
        }

        if (RENDERERS_CACHE.containsKey(block))
            return RENDERERS_CACHE.get(block).values();

        RenderSet set = null;
        RENDERERS_LOCK.lock();
        if (RENDERERS.containsKey(block)) {
            set = new RenderSet();
            RENDERERS.get(block).values().forEach(set::addChild);
        }
        RENDERERS_LOCK.unlock();

        TAG_RENDERERS_LOCK.lock();
        List<TagKey<Block>> tags = connection.registryAccess().registryOrThrow(Registry.BLOCK_REGISTRY).getTagNames().toList();
        for (TagKey<Block> tag : tags) {
            if (TAG_RENDERERS.containsKey(tag)) {
                if (set == null)
                    set = new RenderSet();
                TAG_RENDERERS.get(tag).values().forEach(set::addChild);
            }
        }
        TAG_RENDERERS_LOCK.unlock();

        if (set == null)
            set = EMPTY;
        RENDERERS_CACHE.put(block, set);

        return set.values();
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
