package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimationManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import gg.moonflower.pollen.pinwheel.core.common.network.ModelAnimaMessages;
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
        GeometryModelManager.init();
        GeometryTextureManager.init();
        AnimationManager.init();
    }

    private static void onCommon() {
    }

    private static void onClientPost() {
    }

    private static void onCommonPost() {
    }

    private static void onClientNetworking() {
    }

    private static void onCommonNetworking() {
        ModelAnimaMessages.init();
    }
}
