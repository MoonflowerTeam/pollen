package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.EventListener;
import gg.moonflower.pollen.api.event.events.TickEvent;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.resources.ResourceRegistry;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class Pollen {

    public static final String MOD_ID = "pollen";
    public static final Platform PLATFORM = Platform.builder(Pollen.MOD_ID)
            .commonInit(Pollen::onCommon)
            .clientInit(Pollen::onClient)
            .commonPostInit(Pollen::onCommonPost)
            .clientPostInit(Pollen::onClientPost)
            .commonNetworkInit(Pollen::onCommonNetworking)
            .clientNetworkInit(Pollen::onClientNetworking)
            .build();

    private static void onClient() {
        ResourceRegistry.registerReloadListener(PackType.CLIENT_RESOURCES, (preparationBarrier, resourceManager, profilerFiller, profilerFiller2, backgroundExecutor, gameExecutor) -> preparationBarrier.wait(null).thenRunAsync(() -> System.out.println("Client Reload"), gameExecutor));
        ResourceRegistry.registerReloadListener(PackType.SERVER_DATA, (preparationBarrier, resourceManager, profilerFiller, profilerFiller2, backgroundExecutor, gameExecutor) -> preparationBarrier.wait(null).thenRunAsync(() -> System.out.println("Server Reload"), gameExecutor));
    }

    private static void onCommon() {
        EventDispatcher.register(Pollen.class);
    }

    private static void onClientPost() {
    }

    private static void onCommonPost() {
    }

    private static void onClientNetworking() {
    }

    private static void onCommonNetworking() {
    }
}
