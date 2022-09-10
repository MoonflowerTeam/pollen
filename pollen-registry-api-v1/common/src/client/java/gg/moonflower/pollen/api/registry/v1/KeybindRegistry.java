package gg.moonflower.pollen.api.registry.v1;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import net.minecraft.client.KeyMapping;

public interface KeybindRegistry {

    @ExpectPlatform
    static KeyMapping register(KeyMapping key) {
        return Platform.error();
    }
}
