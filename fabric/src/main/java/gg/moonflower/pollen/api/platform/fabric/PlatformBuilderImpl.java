package gg.moonflower.pollen.api.platform.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;

@ApiStatus.Internal
public class PlatformBuilderImpl {

    public static Platform buildImpl(String modId, Runnable commonInit, Runnable clientInit, Consumer<Platform.ModSetupContext> commonPostInit, Consumer<Platform.ModSetupContext> clientPostInit, Consumer<Platform.DataSetupContext> dataInit) {
        return new FabricPlatform(modId, commonInit, clientInit, commonPostInit, clientPostInit, dataInit);
    }
}
