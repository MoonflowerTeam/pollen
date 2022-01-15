package gg.moonflower.pollen.core.mixin.fabric.sodium;

import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SodiumWorldRenderer.class)
public interface SodiumWorldRendererAccessor {

    @Accessor(remap = false)
    ChunkRenderManager<?> getChunkRenderManager();
}
