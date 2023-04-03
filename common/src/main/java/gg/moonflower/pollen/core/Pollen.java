package gg.moonflower.pollen.core;

import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.impl.platform.PlatformImpl;

public class Pollen {

    public static final String MOD_ID = "pollen";

    public static void init() {
        PlatformImpl.init();
        PollenMessages.init();
    }

    public static void postInit() {}

    public static <T> T expect() {
        throw new AssertionError("Expected platform method");
    }
}
