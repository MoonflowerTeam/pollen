package gg.moonflower.pollen.impl.registry.network.fabric;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.PacketListener;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@ApiStatus.Internal
public interface FabricNetworkWrapper {

    void registerLogin(ResourceLocation channelName, LoginHandler handler);

    void registerPlay(ResourceLocation channelName, PlayHandler handler);

    @FunctionalInterface
    interface LoginHandler {

        CompletableFuture<@Nullable FriendlyByteBuf> receive(PacketListener handler, FriendlyByteBuf buf, Consumer<GenericFutureListener<? extends Future<? super Void>>> listenerAdder);
    }

    @FunctionalInterface
    interface PlayHandler {

        void receive(PacketListener handler, FriendlyByteBuf buf, PacketSender responseSender);
    }
}
