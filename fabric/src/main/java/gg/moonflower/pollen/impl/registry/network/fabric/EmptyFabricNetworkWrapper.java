package gg.moonflower.pollen.impl.registry.network.fabric;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EmptyFabricNetworkWrapper implements FabricNetworkWrapper {

    @Override
    public void registerLogin(ResourceLocation channelName, LoginHandler handler) {
    }

    @Override
    public void registerPlay(ResourceLocation channelName, PlayHandler handler) {
    }
}
