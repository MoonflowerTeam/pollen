package gg.moonflower.pollen.api.network;

import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import net.minecraft.network.FriendlyByteBuf;

import java.io.IOException;

@FunctionalInterface
public interface PacketDeserializer<MSG extends PollinatedPacket<T>, T> {

    MSG deserialize(FriendlyByteBuf buf) throws IOException;
}
