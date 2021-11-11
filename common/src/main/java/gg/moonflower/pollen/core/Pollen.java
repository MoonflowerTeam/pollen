package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvent;
import gg.moonflower.pollen.api.event.events.player.InteractEvent;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimationManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public class Pollen {

    public static final String MOD_ID = "pollen";
    public static final Platform PLATFORM = Platform.builder(Pollen.MOD_ID)
            .commonInit(Pollen::onCommon)
            .clientInit(Pollen::onClient)
            .commonPostInit(Pollen::onCommonPost)
            .clientPostInit(Pollen::onClientPost)
            .build();

    private static MinecraftServer server;

    private static void onClient() {
        GeometryModelManager.init();
        GeometryTextureManager.init();
        AnimationManager.init();
    }

    private static void onCommon() {
        EventDispatcher.register(Pollen::onServerStarting);
        EventDispatcher.register(Pollen::onServerStopped);
        PollenMessages.init();
    }

    private static void onClientPost() {
    }

    private static void onCommonPost() {
    }

    private static void onServerStarting(ServerLifecycleEvent.Starting event) {
        server = event.getServer();
    }

    private static void onServerStopped(ServerLifecycleEvent.Stopped event) {
        server = null;
    }

    public static Optional<MinecraftServer> getRunningServer() {
        return Optional.ofNullable(server);
    }
}
