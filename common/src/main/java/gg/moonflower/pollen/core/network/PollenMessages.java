package gg.moonflower.pollen.core.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.network.v1.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.v1.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.v1.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.network.v1.packet.login.ServerboundAckPacket;
import gg.moonflower.pollen.api.registry.network.v1.PollinatedNetworkRegistry;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.network.play.ClientboundSetAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class PollenMessages {

    public static final PollinatedPlayNetworkChannel PLAY = PollinatedNetworkRegistry.createPlay(new ResourceLocation(Pollen.MOD_ID, "play"), "2");
    public static final PollinatedLoginNetworkChannel LOGIN = PollinatedNetworkRegistry.createLogin(new ResourceLocation(Pollen.MOD_ID, "login"), "1");

    public static void init() {
        // Register Play
        PLAY.register(ClientboundSetAnimationPacket.class, ClientboundSetAnimationPacket::readPacketData, PollinatedPacketDirection.PLAY_CLIENTBOUND);

        // Register Login
        LOGIN.register(ServerboundAckPacket.class, ServerboundAckPacket::new);

        // Register Platform
        registerPlatformPackets();
    }

    @ExpectPlatform
    public static void registerPlatformPackets() {
        throw new AssertionError("Expected platform method");
    }
}
