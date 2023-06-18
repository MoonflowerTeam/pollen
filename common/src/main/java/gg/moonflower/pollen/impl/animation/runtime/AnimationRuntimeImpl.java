package gg.moonflower.pollen.impl.animation.runtime;

import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.pollen.api.animation.v1.controller.StateAnimationController;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

@ApiStatus.Internal
public class AnimationRuntimeImpl {

    private static final SidedAnimationRuntime RUNTIME = ServiceLoader.load(SidedAnimationRuntime.class).findFirst().orElseGet(CommonAnimationRuntime::new);

    public static void addGlobal(MolangEnvironmentBuilder<?> builder) {
        RUNTIME.addGlobal(builder);
    }

    public static void addEntity(MolangEnvironmentBuilder<?> builder, Entity entity, boolean client) {
        RUNTIME.addEntity(builder, entity, client);
    }

    public static StateAnimationController createStateController(AnimationState[] states, MolangRuntime runtime, boolean client) {
        return RUNTIME.createController(states, runtime, client);
    }
}
