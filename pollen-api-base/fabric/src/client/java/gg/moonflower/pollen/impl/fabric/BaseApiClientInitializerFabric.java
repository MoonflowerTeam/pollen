package gg.moonflower.pollen.impl.fabric;

import gg.moonflower.pollen.impl.base.BaseApiClientInitializer;
import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BaseApiClientInitializerFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BaseApiClientInitializer.init();
    }
}
