package gg.moonflower.pollen.core;

import gg.moonflower.pollen.core.Pollen;
import net.fabricmc.api.ModInitializer;

public class PollenFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Pollen.init();
        Pollen.postInit();
    }
}
