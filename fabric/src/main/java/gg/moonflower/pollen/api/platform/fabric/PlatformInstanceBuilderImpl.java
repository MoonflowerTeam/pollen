package gg.moonflower.pollen.api.platform.fabric;

import gg.moonflower.pollen.api.platform.PlatformInstance;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PlatformInstanceBuilderImpl {

    public static PlatformInstance buildImpl(String modId, Runnable commonInit, Runnable clientInit, Runnable commonPostInit, Runnable clientPostInit, Runnable commonNetworkInit, Runnable clientNetworkInit) {
        return new FabricPlatformInstance(modId, commonInit, clientInit, commonPostInit, clientPostInit, commonNetworkInit, clientNetworkInit);
    }
}
