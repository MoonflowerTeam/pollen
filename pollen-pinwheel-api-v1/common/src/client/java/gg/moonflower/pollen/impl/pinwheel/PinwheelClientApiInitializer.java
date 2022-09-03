package gg.moonflower.pollen.impl.pinwheel;

import gg.moonflower.pollen.api.pinwheel.v1.animation.AnimationManager;
import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryModelManager;
import gg.moonflower.pollen.api.pinwheel.v1.geometry.VanillaModelMapping;
import gg.moonflower.pollen.api.pinwheel.v1.texture.GeometryTextureManager;
import org.jetbrains.annotations.ApiStatus;

public final class PinwheelClientApiInitializer {

    private PinwheelClientApiInitializer() {
    }

    @ApiStatus.Internal
    public static void onClient() {
        VanillaModelMapping.load(); // Loads the class to prevent lag spikes in-game
        GeometryModelManager.init();
        GeometryTextureManager.init();
        AnimationManager.init();
    }
}
