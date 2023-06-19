package gg.moonflower.pollen.core;

import gg.moonflower.molangcompiler.api.MolangCompiler;
import gg.moonflower.pinwheel.api.PinwheelMolangCompiler;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.impl.PollenMolangCompiler;
import gg.moonflower.pollen.impl.particle.PollenParticles;
import gg.moonflower.pollen.impl.platform.PlatformImpl;

public class Pollen {

    public static final String MOD_ID = "pollen";

    public static void init() {
        PinwheelMolangCompiler.set(new PollenMolangCompiler(MolangCompiler.DEFAULT_FLAGS, Pollen.class.getClassLoader()));
        PlatformImpl.init();
        PollenMessages.init();
        PollenParticles.PARTICLE_TYPES.register();
    }

    public static void postInit() {
        PollenMessages.init();
    }

    public static <T> T expect() {
        throw new AssertionError("Expected platform method");
    }
}
