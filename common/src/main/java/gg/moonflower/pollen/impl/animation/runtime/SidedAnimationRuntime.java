package gg.moonflower.pollen.impl.animation.runtime;

import gg.moonflower.pollen.api.animation.v1.controller.StateAnimationController;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SidedAnimationRuntime {

    void addGlobal(MolangRuntime.Builder builder);

    void addEntity(MolangRuntime.Builder builder, Entity entity, boolean client);

    StateAnimationController createController(AnimationState[] states, MolangRuntime.Builder builder, boolean client);
}
