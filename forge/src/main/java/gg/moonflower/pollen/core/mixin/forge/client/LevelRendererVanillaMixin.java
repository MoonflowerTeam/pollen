package gg.moonflower.pollen.core.mixin.forge.client;

import gg.moonflower.pollen.core.extensions.CompiledChunkExtension;
import gg.moonflower.pollen.core.extensions.LevelRendererExtension;
import gg.moonflower.pollen.core.mixin.client.LevelRendererRenderChunkInfoAccessor;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.stream.Stream;

@Mixin(LevelRenderer.class)
public class LevelRendererVanillaMixin implements LevelRendererExtension {

    @Shadow
    @Final
    private ObjectArrayList<LevelRenderer.RenderChunkInfo> renderChunksInFrustum;

    @Override
    public Stream<BlockPos> pollen_getBlockRenderers() {
        return this.renderChunksInFrustum.stream().flatMap(info -> ((CompiledChunkExtension) ((LevelRendererRenderChunkInfoAccessor) info).getChunk().getCompiledChunk()).pollen_getBlockRenderPositions().stream());
    }
}
