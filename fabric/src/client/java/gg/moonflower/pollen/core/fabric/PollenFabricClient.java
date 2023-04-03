package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.core.PollenClient;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.network.fabric.FabricClientLoginPacketHandlerImpl;
import gg.moonflower.pollen.core.network.fabric.PollenMessagesImpl;
import net.fabricmc.api.ClientModInitializer;

public class PollenFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PollenClient.init();
        PollenClient.postInit();

        PollenMessages.PLAY.setClientHandler(new FabricClientLoginPacketHandlerImpl());
    }
}
