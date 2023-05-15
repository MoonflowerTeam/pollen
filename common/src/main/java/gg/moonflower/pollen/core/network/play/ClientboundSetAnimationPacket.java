package gg.moonflower.pollen.core.network.play;

import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketContext;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public record ClientboundSetAnimationPacket(int entityId,
                                            FriendlyByteBuf data) implements PollinatedPacket<PollenClientMessageHandler> {

    public static ClientboundSetAnimationPacket readPacketData(FriendlyByteBuf buf) {
        int entityId = buf.readVarInt();

        int remaining = buf.readableBytes();
        if (remaining < 0 || remaining > 32767) {
            throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
        }

        FriendlyByteBuf data = new FriendlyByteBuf(buf.readBytes(remaining));

        return new ClientboundSetAnimationPacket(entityId, data);
    }

    @Override
    public void writePacketData(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entityId);
        buf.writeBytes(this.data);
    }

    @Override
    public void processPacket(PollenClientMessageHandler handler, PollinatedPacketContext ctx) {
        handler.handleSetAnimation(this, ctx);
    }
}
