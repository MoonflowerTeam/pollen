package gg.moonflower.pollen.api.platform.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class PlatformInstanceBuilderImpl {

    public static Platform buildImpl(String modId, Runnable commonInit, Runnable clientInit, Runnable commonPostInit, Runnable clientPostInit, Runnable commonNetworkInit, Runnable clientNetworkInit) {
        return new FabricPlatform(modId, commonInit, clientInit, commonPostInit, clientPostInit, commonNetworkInit, clientNetworkInit);
    }
}
