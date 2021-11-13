package gg.moonflower.pollen.core.network.fabric;

import gg.moonflower.pollen.core.network.PollenClientPlayPacketHandlerImpl;
import gg.moonflower.pollen.core.network.play.PollenClientPlayPacketHandler;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenMessagesImpl {

    public static void registerPlatformPackets() {
    }

    public static PollenClientPlayPacketHandler createClientPlayHandler() {
        return new PollenClientPlayPacketHandlerImpl();
    }
}
