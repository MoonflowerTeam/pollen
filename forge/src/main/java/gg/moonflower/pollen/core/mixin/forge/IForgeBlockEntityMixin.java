package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.blockentity.PollenBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraftforge.common.extensions.IForgeBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IForgeBlockEntity.class)
public abstract class IForgeBlockEntityMixin {

    @Inject(method = "onDataPacket", at = @At("HEAD"), remap = false)
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, CallbackInfo ci) {
        if (this instanceof PollenBlockEntity blockEntity) {
            blockEntity.onDataPacket(net, pkt);
            ci.cancel();
        }
    }

    @Inject(method = "handleUpdateTag", at = @At("HEAD"), remap = false)
    public void handleUpdateTag(CompoundTag tag, CallbackInfo ci) {
        if (this instanceof PollenBlockEntity blockEntity) {
            blockEntity.handleUpdateTag(tag);
            ci.cancel();
        }
    }
}
