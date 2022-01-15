package gg.moonflower.pollen.core.mixin.forge.client;

import gg.moonflower.pollen.core.extensions.CompiledChunkExtension;
import gg.moonflower.pollen.core.extensions.LevelRendererExtension;
import gg.moonflower.pollen.core.mixin.client.LevelRendererRenderChunkInfoAccessor;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.stream.Stream;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererVanillaMixin implements LevelRendererExtension {

    @Shadow
    @Final
    private ObjectList<LevelRenderer.RenderChunkInfo> renderChunks;

    @Override
    public Stream<BlockPos> pollen_getBlockRenderers() {
        return this.renderChunks.stream().flatMap(info -> ((CompiledChunkExtension) ((LevelRendererRenderChunkInfoAccessor) info).getChunk().getCompiledChunk()).pollen_getBlockRenderPositions().stream());
    }
}
