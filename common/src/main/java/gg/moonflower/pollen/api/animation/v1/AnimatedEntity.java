package gg.moonflower.pollen.api.animation.v1;

import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pollen.api.animation.v1.controller.ManualAnimationController;
import gg.moonflower.pollen.impl.animation.ManualAnimationControllerImpl;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.world.entity.Entity;

public interface AnimatedEntity {

    AnimationController getAnimationController();

    static ManualAnimationController createManual(AnimationData[] animations) {
        return new ManualAnimationControllerImpl(animations);
    }
}
