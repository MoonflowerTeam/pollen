package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.blockentity.PollenBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LevelChunk.class)
public class LevelChunkMixin {

    @Redirect(method = "method_31716", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;load(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void replaceWithPacketData(BlockEntity instance, CompoundTag compoundTag) {
        if (instance instanceof PollenBlockEntity blockEntity) {
            blockEntity.handleUpdateTag(compoundTag);
            return;
        }
        instance.load(compoundTag);
    }
}
