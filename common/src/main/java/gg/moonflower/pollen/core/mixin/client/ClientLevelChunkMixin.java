package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.core.extensions.ClientLevelChunkExtension;
import gg.moonflower.pollen.core.extensions.ClientLevelExtension;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Consumer;

@Mixin(LevelChunk.class)
public abstract class ClientLevelChunkMixin extends ChunkAccess implements ClientLevelChunkExtension {

    @Shadow
    @Final
    private Level level;
    @Unique
    private final Map<BlockPos, Set<TickableBlockRenderer>> tickableBlockRenderers = new HashMap<>();

    private ClientLevelChunkMixin(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> registry, long l, @Nullable LevelChunkSection[] levelChunkSections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, registry, l, levelChunkSections, blendingData);
    }

    @Unique
    private void addRenderers(BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        List<BlockRenderer> renderers = BlockRendererRegistry.get(block);
        if (!renderers.isEmpty() && renderers.stream().anyMatch(renderer -> renderer.getRenderShape(state) != RenderShape.MODEL)) {
            BlockPos immutablePos = pos.immutable();
            for (BlockRenderer renderer : renderers)
                if (renderer instanceof TickableBlockRenderer)
                    this.tickableBlockRenderers.computeIfAbsent(immutablePos, __ -> new HashSet<>()).add((TickableBlockRenderer) renderer);
            ((ClientLevelExtension) this.level).pollen_scheduleTick(immutablePos);
        }
    }

    @Inject(method = "setBlockState", at = @At("TAIL"))
    public void setBlockState(BlockPos pos, BlockState state, boolean isMoving, CallbackInfoReturnable<BlockState> cir) {
        if (!this.level.isClientSide()) // Renderers are client only
            return;

        this.tickableBlockRenderers.remove(pos);
        this.addRenderers(pos, state);
        ((ClientLevelExtension) this.level).pollen_scheduleTick(pos, cir.getReturnValue());
    }

    @Inject(method = "replaceWithPacketData", at = @At("TAIL"))
    public void updateBlockRenderers(FriendlyByteBuf friendlyByteBuf, CompoundTag compoundTag, Consumer<ClientboundLevelChunkPacketData.BlockEntityTagOutput> consumer, CallbackInfo ci) {
        if (!this.level.isClientSide()) // Renderers are client only
            return;

        for (LevelChunkSection section : this.sections) {
            if (section.hasOnlyAir())
                continue;

            for (BlockPos pos : BlockPos.betweenClosed(0, 0, 0, 15, 15, 15)) {
                this.addRenderers(pos.offset(this.chunkPos.getMinBlockX(), section.bottomBlockY(), this.chunkPos.getMinBlockZ()), section.getBlockState(pos.getX(), pos.getY(), pos.getZ()));
            }
        }
    }

    @Override
    public Map<BlockPos, Set<TickableBlockRenderer>> pollen_getTickableBlockRenderers() {
        return tickableBlockRenderers;
    }
}
