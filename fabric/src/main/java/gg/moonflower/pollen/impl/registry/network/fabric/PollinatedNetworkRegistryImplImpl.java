package gg.moonflower.pollen.impl.registry.network.fabric;

import gg.moonflower.pollen.api.network.v1.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.v1.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.impl.network.PollinatedFabricLoginChannel;
import gg.moonflower.pollen.impl.network.PollinatedFabricPlayChannel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

@ApiStatus.Internal
public class PollinatedNetworkRegistryImplImpl {

    public static final FabricNetworkWrapper WRAPPER = ServiceLoader.load(FabricNetworkWrapper.class).findFirst().orElseGet(EmptyFabricNetworkWrapper::new);

    public static PollinatedPlayNetworkChannel createPlay(ResourceLocation channelId, String version) {
        return new PollinatedFabricPlayChannel(channelId);
    }

    public static PollinatedLoginNetworkChannel createLogin(ResourceLocation channelId, String version) {
        return new PollinatedFabricLoginChannel(channelId);
    }
}
