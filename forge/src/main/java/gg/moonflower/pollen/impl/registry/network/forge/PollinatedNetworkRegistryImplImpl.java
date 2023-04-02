package gg.moonflower.pollen.impl.registry.network.forge;

import gg.moonflower.pollen.api.network.v1.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.v1.PollinatedPlayNetworkChannel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollinatedNetworkRegistryImplImpl {

    public static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version) {
        return new PollinatedForgePlayChannel(NetworkRegistry.newSimpleChannel(channelId, () -> version, version::equals, version::equals));
    }

    public static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version) {
        return new PollinatedForgeLoginChannel(NetworkRegistry.newSimpleChannel(channelId, () -> version, version::equals, version::equals));
    }
}
