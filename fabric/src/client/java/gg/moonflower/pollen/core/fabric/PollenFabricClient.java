package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.core.PollenClient;
import net.fabricmc.api.ClientModInitializer;

public class PollenFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PollenClient.init();
        PollenClient.postInit();
    }
}
