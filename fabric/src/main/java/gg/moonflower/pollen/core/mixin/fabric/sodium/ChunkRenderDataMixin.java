package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.core.extensions.fabric.sodium.ChunkRenderDataExtension;
import me.jellysquid.mods.sodium.client.render.chunk.data.ChunkRenderData;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

@Mixin(ChunkRenderData.class)
public class ChunkRenderDataMixin implements ChunkRenderDataExtension {

    @Unique
    private final Set<BlockPos> renderableBlocks = new HashSet<>();
    @Unique
    private final Set<BlockPos> tickingBlocks = new HashSet<>();

    @Override
    public Set<BlockPos> pollen_getBlockRenderPositions() {
        return this.renderableBlocks;
    }

    @Override
    public Set<BlockPos> pollen_getTickingBlockRenderPositions() {
        return tickingBlocks;
    }
}
