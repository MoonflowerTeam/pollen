package gg.moonflower.pollen.api.platform.forge;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class PlatformBuilderImpl {

    public static Platform buildImpl(String modId, Runnable commonInit, Runnable clientInit, Runnable commonPostInit, Runnable clientPostInit) {
        return new ForgePlatform(modId, FMLJavaModLoadingContext.get().getModEventBus(), commonInit, clientInit, commonPostInit, clientPostInit);
    }
}
