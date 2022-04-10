package gg.moonflower.pollen.api.platform.forge;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public final class PlatformBuilderImpl {

    public static Platform buildImpl(String modId, Runnable commonInit, Supplier<Runnable> clientInit, Supplier<Runnable> serverInit, Consumer<Platform.ModSetupContext> commonPostInit, Supplier<Consumer<Platform.ModSetupContext>> clientPostInit, Supplier<Consumer<Platform.ModSetupContext>> serverPostInit, Consumer<Platform.DataSetupContext> dataInit) {
        return new ForgePlatform(modId, FMLJavaModLoadingContext.get().getModEventBus(), commonInit, clientInit, serverInit, commonPostInit, clientPostInit, serverPostInit, dataInit);
    }
}
