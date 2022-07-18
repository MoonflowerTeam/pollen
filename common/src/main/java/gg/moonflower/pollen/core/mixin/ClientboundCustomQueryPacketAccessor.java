package gg.moonflower.pollen.core.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ClientboundCustomQueryPacket.class)
public interface ClientboundCustomQueryPacketAccessor {
    @Accessor
    int getTransactionId();

    @Accessor
    void setTransactionId(int id);

    @Accessor
    FriendlyByteBuf getData();

    @Accessor
    void setData(FriendlyByteBuf buf);

    @Accessor
    ResourceLocation getIdentifier();

    @Accessor
    void setIdentifier(ResourceLocation identifier);
}
