package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.fabric.PollinatedFabricLoginChannel;
import gg.moonflower.pollen.api.network.fabric.PollinatedFabricPlayChannel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public class NetworkRegistryImpl {

    public static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        return new PollinatedFabricPlayChannel(channelId, clientFactory, serverFactory);
    }

    public static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        return new PollinatedFabricLoginChannel(channelId, clientFactory, serverFactory);
    }
}
