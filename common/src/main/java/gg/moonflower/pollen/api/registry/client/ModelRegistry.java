package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.resources.ResourceLocation;

public final class ModelRegistry {

    private ModelRegistry() {
    }

    @ExpectPlatform
    public static void registerSpecial(ResourceLocation location) {
        Platform.error();
    }
}