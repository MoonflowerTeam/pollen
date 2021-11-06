package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.platform.PlatformInstance;

public class Pollen {
    public static final String MOD_ID = "pollen";
    public static final PlatformInstance PLATFORM = PlatformInstance.builder(MOD_ID)
            .commonInit(Pollen::onCommon)
            .clientInit(Pollen::onClient)
            .commonPostInit(Pollen::onCommonPost)
            .clientPostInit(Pollen::onClientPost)
            .commonNetworkInit(Pollen::onCommonNetworking)
            .clientNetworkInit(Pollen::onClientNetworking)
            .build();

    private static void onClient() {
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
    }
}
