package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.blockentity.PollenBlockEntity;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundRespawnPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

    @Final
    @Shadow
    private Minecraft minecraft;
    @Shadow
    @Final
    private Connection connection;

    @Unique
    private ClientboundBlockEntityDataPacket pollen$beDataPacket;

    @Inject(method = "handleRespawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;setServerBrand(Ljava/lang/String;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void handleRespawn(ClientboundRespawnPacket pkt, CallbackInfo ci, ResourceKey<Level> dimensionKey, Holder<DimensionType> dimensionType, LocalPlayer oldPlayer, int oldPlayerId, String serverBrand, LocalPlayer newPlayer) {
        ClientNetworkEvents.RESPAWN.invoker().respawn(this.minecraft.gameMode, oldPlayer, newPlayer, newPlayer.connection.getConnection());
    }

    @Inject(method = "handleBlockEntityData", at = @At("HEAD"))
    public void captureHandleBlockEntityData(ClientboundBlockEntityDataPacket pkt, CallbackInfo ci) {
        this.pollen$beDataPacket = pkt;
    }

    @Redirect(method = "method_38542", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/entity/BlockEntity;load(Lnet/minecraft/nbt/CompoundTag;)V"))
    public void handleBlockEntityData(BlockEntity instance, CompoundTag compoundTag) {
        if (instance instanceof PollenBlockEntity blockEntity) {
            blockEntity.onDataPacket(this.connection, this.pollen$beDataPacket);
        } else {
            instance.load(compoundTag);
        }
    }
}
