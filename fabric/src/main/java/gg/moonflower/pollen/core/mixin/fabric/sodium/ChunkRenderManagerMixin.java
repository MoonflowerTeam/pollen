package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.core.extensions.fabric.sodium.ChunkRenderDataExtension;
import gg.moonflower.pollen.core.extensions.fabric.sodium.ChunkRenderManagerExtension;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(RenderSectionManager.class)
public class ChunkRenderManagerMixin implements ChunkRenderManagerExtension {

    @Unique
    private final ObjectList<RenderSection> renderChunks = new ObjectArrayList<>();

    @Inject(method = "addChunkToVisible", at = @At("HEAD"), remap = false)
    public void addChunkToVisible(RenderSection render, CallbackInfo ci) {
        this.renderChunks.add(render);
    }

    @Inject(method = "resetLists", at = @At("TAIL"), remap = false)
    public void resetLists(CallbackInfo ci) {
        this.renderChunks.clear();
    }

    @Override
    public Stream<BlockPos> pollen_getBlockRenderPositions() {
        return this.renderChunks.stream().flatMap(container -> ((ChunkRenderDataExtension) container.getData()).pollen_getBlockRenderPositions().stream());
    }

    @Override
    public Stream<BlockPos> pollen_getTickingBlockRenderPositions() {
        return this.renderChunks.stream().flatMap(container -> ((ChunkRenderDataExtension) container.getData()).pollen_getTickingBlockRenderPositions().stream());
    }
}
