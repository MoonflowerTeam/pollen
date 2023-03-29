package gg.moonflower.pollen.core;

public class Pollen {

    public static final String MOD_ID = "pollen";

    public static void init() {}

    public static void postInit() {}

    public static <T> T expect() {
        throw new AssertionError("Expected platform method");
    }
}
