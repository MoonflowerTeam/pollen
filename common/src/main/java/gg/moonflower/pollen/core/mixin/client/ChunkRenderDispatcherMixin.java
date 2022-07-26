package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.core.extensions.ChunkRenderExtensions;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.concurrent.CompletableFuture;

@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public class ChunkRenderDispatcherMixin {

    @Inject(method = "doTask", at = @At(value = "INVOKE", target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    public void copyCompileResults(ChunkBufferBuilderPack chunkBufferBuilderPack, CallbackInfoReturnable<CompletableFuture<ChunkRenderDispatcher.ChunkTaskResult>> cir, Vec3 vec, float x, float y, float z, ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults compileResults, ChunkRenderDispatcher.CompiledChunk compiledChunk) {
        ((ChunkRenderExtensions) compiledChunk).pollen_getBlockRenderPositions().addAll(((ChunkRenderExtensions) (Object) compileResults).pollen_getBlockRenderPositions());
        ((ChunkRenderExtensions) compiledChunk).pollen_getTickingBlockRenderers().addAll(((ChunkRenderExtensions) (Object) compileResults).pollen_getTickingBlockRenderers());
    }
}
