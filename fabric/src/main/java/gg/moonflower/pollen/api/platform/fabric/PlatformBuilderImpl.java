package gg.moonflower.pollen.api.platform.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class PlatformBuilderImpl {

    public static Platform buildImpl(String modId, Runnable commonInit, Runnable clientInit, Runnable commonPostInit, Runnable clientPostInit) {
        return new FabricPlatform(modId, commonInit, clientInit, commonPostInit, clientPostInit);
    }
}
