package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import gg.moonflower.pollen.pinwheel.core.client.BlockDataStorage;
import gg.moonflower.pollen.pinwheel.core.client.DataContainerImpl;
import gg.moonflower.pollen.core.extensions.ClientLevelExtensions;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin extends Level implements ClientLevelExtensions {

    @Shadow
    @Final
    private ClientChunkCache chunkSource;
    @Unique
    private final Map<ChunkPos, Set<BlockPos>> blockRenderers = new HashMap<>();
    @Unique
    private final Map<BlockPos, BlockState> pendingUpdates = new HashMap<>();
    @Unique
    private final Map<BlockPos, BlockState> updates = new HashMap<>();
    @Unique
    private final DataContainerImpl dataContainer = new DataContainerImpl((ClientLevel) (Object) this);

    private ClientLevelMixin(WritableLevelData writableLevelData, ResourceKey<Level> resourceKey, DimensionType dimensionType, Supplier<ProfilerFiller> supplier, boolean bl, boolean bl2, long l) {
        super(writableLevelData, resourceKey, dimensionType, supplier, bl, bl2, l);
    }

    @Unique
    private void scheduleTick(BlockPos pos, BlockState state){
        this.pendingUpdates.put(pos.immutable(), state);
    }

    @Override
    public void pollen_scheduleTick(BlockPos pos) {
        this.scheduleTick(pos, this.getBlockState(pos));
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        boolean flag = super.setBlock(pos, state, flags, recursionLeft);
        BlockDataStorage.get(this).update(state, pos);

        BlockRenderer renderer = BlockRendererRegistry.get(state.getBlock());
        ChunkPos chunkPos = new ChunkPos(pos);
        if (renderer instanceof TickableBlockRenderer) {
            this.blockRenderers.computeIfAbsent(chunkPos, __ -> new HashSet<>()).add(pos.immutable());
        } else if (this.blockRenderers.containsKey(chunkPos)) {
            this.blockRenderers.get(chunkPos).remove(pos);
        }

        if (flag)
            this.scheduleTick(pos, state);
        return flag;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        this.updates.clear();
        this.updates.putAll(this.pendingUpdates);
        this.pendingUpdates.clear();

        AtomicReferenceArray<LevelChunk> chunks = ((ClientChunkCacheStorageAccessor) (Object) ((ClientChunkCacheAccessor) this.chunkSource).getStorage()).getChunks();
        for (int i = 0; i < chunks.length(); i++) {
            LevelChunk chunk = chunks.get(i);
            if (chunk == null)
                continue;
            this.blockRenderers.computeIfAbsent(chunk.getPos(), chunkPos -> {
                Set<BlockPos> positions = new HashSet<>();
                for (BlockPos pos : BlockPos.betweenClosed(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), this.getMaxBuildHeight(), chunkPos.getMaxBlockZ())) {
                    BlockState state = chunk.getBlockState(pos);
                    BlockRenderer renderer = BlockRendererRegistry.get(state.getBlock());
                    if (renderer != null && renderer.getRenderShape(state) != RenderShape.MODEL) {
                        BlockPos immutablePos = pos.immutable();
                        positions.add(immutablePos);
                        this.pollen_scheduleTick(immutablePos);
                    }
                }
                return positions;
            }).forEach(pos -> {
                BlockState state = this.getBlockState(pos);
                BlockRenderer renderer = BlockRendererRegistry.get(state.getBlock());
                if (renderer != null) {
                    BlockState oldState = this.updates.remove(pos);
                    if (oldState != null)
                        renderer.receiveUpdate(this, pos, oldState, state, this.dataContainer.get(pos));
                    if (renderer instanceof TickableBlockRenderer)
                        ((TickableBlockRenderer) renderer).tick(this, pos, this.dataContainer.get(pos));
                }
            });
        }

        this.updates.forEach((pos, state) -> {
            BlockRenderer renderer = BlockRendererRegistry.get(state.getBlock());
            if (renderer != null)
                renderer.receiveUpdate(this, pos, state, this.getBlockState(pos), this.dataContainer.get(pos));
        });
    }

    @Inject(method = "unload", at = @At("TAIL"))
    public void onChunkLoaded(LevelChunk chunk, CallbackInfo ci) {
        this.blockRenderers.remove(chunk.getPos());
        BlockDataStorage.get(this).invalidateChunk(chunk.getPos());
    }
}
