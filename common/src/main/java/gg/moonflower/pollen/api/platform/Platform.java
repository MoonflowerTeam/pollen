package gg.moonflower.pollen.api.platform;

public final class Platform {

    public static <T> T error() {
        throw new AssertionError();
    }
}
