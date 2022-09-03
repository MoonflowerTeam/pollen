package gg.moonflower.pollen.impl.pinwheel.fabric;

import gg.moonflower.pollen.impl.pinwheel.PinwheelClientApiInitializer;
import net.fabricmc.api.ClientModInitializer;

public class PinwheelClientFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PinwheelClientApiInitializer.onClient();
    }
}
