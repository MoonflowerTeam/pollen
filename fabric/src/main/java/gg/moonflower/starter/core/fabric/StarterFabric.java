package gg.moonflower.starter.core.fabric;

import gg.moonflower.starter.core.Starter;
import net.fabricmc.api.ModInitializer;

public class StarterFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Starter.init();
    }
}
