package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.core.extensions.fabric.sodium.ChunkRenderDataExtension;
import gg.moonflower.pollen.core.extensions.fabric.sodium.ChunkRenderManagerExtension;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkGraphicsState;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderContainer;
import me.jellysquid.mods.sodium.client.render.chunk.ChunkRenderManager;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(ChunkRenderManager.class)
public class ChunkRenderManagerMixin<T extends ChunkGraphicsState> implements ChunkRenderManagerExtension {

    @Unique
    private final ObjectList<ChunkRenderContainer<T>> renderChunks = new ObjectArrayList<>();

    @Inject(method = "addChunkToRenderLists", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/render/chunk/ChunkRenderContainer;isTickable()Z", shift = At.Shift.BEFORE), remap = false)
    public void addChunkToRenderLists(ChunkRenderContainer<T> state, CallbackInfo ci) {
        this.renderChunks.add(state);
    }

    @Inject(method = "reset", at = @At("TAIL"), remap = false)
    public void reset(CallbackInfo ci) {
        this.renderChunks.clear();
    }

    @Override
    public Stream<BlockPos> pollen_getBlockRenderPositions() {
        return this.renderChunks.stream().flatMap(container -> ((ChunkRenderDataExtension) container.getData()).pollen_getBlockRenderPositions().stream());
    }
}
