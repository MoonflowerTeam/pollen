package gg.moonflower.pollen.core.network;

import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.registry.NetworkRegistry;
import gg.moonflower.pollen.api.network.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.network.packet.login.ServerboundAckPacket;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.network.play.ClientboundSyncAnimationPacket;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class PollenMessages {

    public static final PollinatedPlayNetworkChannel PLAY = NetworkRegistry.createPlay(new ResourceLocation(Pollen.MOD_ID, "play"), "1", () -> PollenClientPlayPacketHandlerImpl::new, () -> Object::new);
    public static final PollinatedLoginNetworkChannel LOGIN = NetworkRegistry.createLogin(new ResourceLocation(Pollen.MOD_ID, "login"), "1", () -> Object::new, () -> Object::new);

    public static void init() {
        PLAY.register(ClientboundSyncAnimationPacket.class, ClientboundSyncAnimationPacket::new, PollinatedPacketDirection.PLAY_CLIENTBOUND);

        LOGIN.register(ServerboundAckPacket.class, ServerboundAckPacket::new);
    }
}
