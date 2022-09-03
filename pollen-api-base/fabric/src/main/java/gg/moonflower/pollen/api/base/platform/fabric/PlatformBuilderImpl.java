package gg.moonflower.pollen.api.base.platform.fabric;

import gg.moonflower.pollen.api.base.platform.Platform;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PlatformBuilderImpl {

    public static Platform buildImpl(String modId, Runnable commonInit, Supplier<Runnable> clientInit, Supplier<Runnable> serverInit, Consumer<Platform.ModSetupContext> commonPostInit, Supplier<Consumer<Platform.ModSetupContext>> clientPostInit, Supplier<Consumer<Platform.ModSetupContext>> serverPostInit, Consumer<Platform.DataSetupContext> dataInit) {
        return new FabricPlatform(modId, commonInit, clientInit, serverInit, commonPostInit, clientPostInit, serverPostInit, dataInit);
    }
}
