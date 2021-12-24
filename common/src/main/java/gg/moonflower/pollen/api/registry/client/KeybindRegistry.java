package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.KeyMapping;

public final class KeybindRegistry {

    private KeybindRegistry() {
    }

    @ExpectPlatform
    public static KeyMapping register(KeyMapping key) {
        return Platform.error();
    }
}
