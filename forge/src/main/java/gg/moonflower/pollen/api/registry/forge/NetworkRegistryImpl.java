package gg.moonflower.pollen.api.registry.forge;

import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.forge.PollinatedForgeLoginChannel;
import gg.moonflower.pollen.api.network.forge.PollinatedForgePlayChannel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public class NetworkRegistryImpl {

    public static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        return new PollinatedForgePlayChannel(NetworkRegistry.newSimpleChannel(channelId, () -> version, version::equals, version::equals), clientFactory, serverFactory);
    }

    public static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version, Supplier<Object> clientFactory, Supplier<Object> serverFactory) {
        return new PollinatedForgeLoginChannel(NetworkRegistry.newSimpleChannel(channelId, () -> version, version::equals, version::equals), clientFactory, serverFactory);
    }
}
