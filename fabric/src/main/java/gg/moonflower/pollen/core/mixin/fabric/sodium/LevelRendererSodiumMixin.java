package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.core.extensions.LevelRendererExtension;
import gg.moonflower.pollen.core.extensions.fabric.sodium.ChunkRenderManagerExtension;
import me.jellysquid.mods.sodium.client.render.SodiumWorldRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.stream.Stream;

@Mixin(LevelRenderer.class)
public class LevelRendererSodiumMixin implements LevelRendererExtension {

    @Override
    public Stream<BlockPos> pollen_getBlockRenderers() {
        return ((ChunkRenderManagerExtension) ((SodiumWorldRendererAccessor) SodiumWorldRenderer.getInstance()).getChunkRenderManager()).pollen_getBlockRenderPositions();
    }
}
