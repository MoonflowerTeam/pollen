package gg.moonflower.pollen.core.network;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.network.PollinatedLoginNetworkChannel;
import gg.moonflower.pollen.api.network.PollinatedPlayNetworkChannel;
import gg.moonflower.pollen.api.network.packet.PollinatedPacketDirection;
import gg.moonflower.pollen.api.network.packet.login.ServerboundAckPacket;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.registry.NetworkRegistry;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.network.login.ClientboundSyncPlayerDataKeysPacket;
import gg.moonflower.pollen.core.network.login.PollenClientLoginPacketHandler;
import gg.moonflower.pollen.core.network.play.ClientboundSyncAnimationPacket;
import gg.moonflower.pollen.core.network.play.ClientboundUpdateSettingsPacket;
import gg.moonflower.pollen.core.network.play.PollenClientPlayPacketHandler;
import gg.moonflower.pollen.core.network.play.ServerboundUpdateSettingsPacket;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class PollenMessages {

    public static final PollinatedPlayNetworkChannel PLAY = NetworkRegistry.createPlay(new ResourceLocation(Pollen.MOD_ID, "play"), "2", PollenMessages::createClientPlayHandler, PollenServerPlayPacketHandlerImpl::new);
    public static final PollinatedLoginNetworkChannel LOGIN = NetworkRegistry.createLogin(new ResourceLocation(Pollen.MOD_ID, "login"), "1", PollenMessages::createClientLoginHandler, Object::new);

    public static void init() {
        PLAY.register(ClientboundSyncAnimationPacket.class, ClientboundSyncAnimationPacket::new, PollinatedPacketDirection.PLAY_CLIENTBOUND);
        PLAY.register(ClientboundUpdateSettingsPacket.class, ClientboundUpdateSettingsPacket::new, PollinatedPacketDirection.PLAY_CLIENTBOUND);
        PLAY.register(ServerboundUpdateSettingsPacket.class, ServerboundUpdateSettingsPacket::new, PollinatedPacketDirection.PLAY_SERVERBOUND);

        LOGIN.register(ServerboundAckPacket.class, ServerboundAckPacket::new);
        LOGIN.registerLogin(ClientboundSyncPlayerDataKeysPacket.class, ClientboundSyncPlayerDataKeysPacket::new, (Supplier<ClientboundSyncPlayerDataKeysPacket>) ClientboundSyncPlayerDataKeysPacket::new);

        registerPlatformPackets();
    }

    @ExpectPlatform
    public static PollenClientPlayPacketHandler createClientPlayHandler() {
        return Platform.error();
    }

    @ExpectPlatform
    public static PollenClientLoginPacketHandler createClientLoginHandler() {
        return Platform.error();
    }

    @ExpectPlatform
    public static void registerPlatformPackets() {
        Platform.error();
    }
}
