package gg.moonflower.pollen.impl.animation.runtime;

import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.pollen.api.animation.v1.controller.IdleAnimationController;
import gg.moonflower.pollen.api.animation.v1.controller.PollenAnimationController;
import gg.moonflower.pollen.api.animation.v1.controller.StateAnimationController;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface SidedAnimationRuntime {

    void addGlobal(MolangEnvironmentBuilder<?> builder);

    void addEntity(MolangEnvironmentBuilder<?> builder, Entity entity, boolean client);

    StateAnimationController createController(AnimationState[] states, MolangRuntime runtime, boolean client);

    IdleAnimationController createIdleController(PollenAnimationController controller);
}
