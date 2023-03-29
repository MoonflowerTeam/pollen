package gg.moonflower.pollen.client.fabric;

import gg.moonflower.pollen.client.PollenClient;
import net.fabricmc.api.ClientModInitializer;

public class PollenFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PollenClient.init();
        PollenClient.postInit();
    }
}
