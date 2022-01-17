package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.core.extensions.LevelRendererExtension;
import gg.moonflower.pollen.core.extensions.fabric.sodium.ChunkRenderManagerExtension;
import me.jellysquid.mods.sodium.client.world.WorldRendererExtended;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

import java.util.stream.Stream;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererSodiumMixin implements LevelRendererExtension {

    @Override
    public Stream<BlockPos> pollen_getBlockRenderers() {
        return ((ChunkRenderManagerExtension) ((SodiumWorldRendererAccessor) ((WorldRendererExtended)this).getSodiumWorldRenderer()).getRenderSectionManager()).pollen_getBlockRenderPositions();
    }

    @Override
    public Stream<BlockPos> pollen_getTickingBlockRenderers() {
        return ((ChunkRenderManagerExtension) ((SodiumWorldRendererAccessor) ((WorldRendererExtended)this).getSodiumWorldRenderer()).getRenderSectionManager()).pollen_getTickingBlockRenderPositions();
    }
}
