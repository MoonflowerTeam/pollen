package gg.moonflower.pollen.api.network.forge;

import gg.moonflower.pollen.api.network.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.packet.PollinatedPacket;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedForgePlayChannel extends PollinatedNetworkChannelImpl implements PollinatedPlayNetworkChannel {

    public PollinatedForgePlayChannel(SimpleChannel channel, Supplier<Supplier<Object>> clientFactory, Supplier<Supplier<Object>> serverFactory) {
        super(channel, clientFactory, serverFactory);
    }

    @Override
    public void sendTo(ServerPlayer player, PollinatedPacket<?> message) {
        this.channel.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    @Override
    public void sendTo(ServerLevel level, PollinatedPacket<?> message) {
        this.channel.send(PacketDistributor.DIMENSION.with(level::dimension), message);
    }

    @Override
    public void sendToNear(ServerLevel level, double x, double y, double z, double radius, PollinatedPacket<?> message) {
        this.channel.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, radius * radius, level.dimension())), message);
    }

    @Override
    public void sendToAll(MinecraftServer server, PollinatedPacket<?> message) {
        this.channel.send(PacketDistributor.ALL.noArg(), message);
    }

    @Override
    public void sendToTracking(Entity entity, PollinatedPacket<?> message) {
        this.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), message);
    }

    @Override
    public void sendToTracking(ServerLevel level, BlockPos pos, PollinatedPacket<?> message) {
        this.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)), message);
    }

    @Override
    public void sendToTracking(ServerLevel level, ChunkPos pos, PollinatedPacket<?> message) {
        this.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(pos.x, pos.z)), message);
    }

    @Override
    public void sendToTrackingAndSelf(Entity entity, PollinatedPacket<?> message) {
        this.channel.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), message);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void sendToServer(PollinatedPacket<?> message) {
        this.channel.sendToServer(message);
    }

    @Override
    public <MSG extends PollinatedPacket<T>, T> void register(Class<MSG> clazz, Function<FriendlyByteBuf, MSG> deserializer, @Nullable PollinatedPacketDirection direction) {
        this.getMessageBuilder(clazz, deserializer, direction).add();
    }
}
