package gg.moonflower.pollen.core.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundCustomQueryPacket.class)
public interface ServerboundCustomQueryPacketAccessor {
    @Accessor
    int getTransactionId();

    @Nullable
    @Accessor
    FriendlyByteBuf getData();
}
