package gg.moonflower.pollen.api.animation.v1;

import gg.moonflower.pollen.api.animation.v1.controller.StateAnimationController;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;
import gg.moonflower.pollen.impl.animation.runtime.AnimationRuntimeImpl;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.world.entity.Entity;

public interface AnimationRuntime {

    static void addGlobal(MolangRuntime.Builder builder) {
        AnimationRuntimeImpl.addGlobal(builder);
    }

    static void addEntity(MolangRuntime.Builder builder, Entity entity, boolean client) {
        AnimationRuntimeImpl.addEntity(builder, entity, client);
    }

    static StateAnimationController createState(AnimationState[] states, MolangRuntime.Builder builder, boolean client) {
        return AnimationRuntimeImpl.createStateController(states, builder, client);
    }

    static StateAnimationController createState(AnimationState[] states, Entity entity) {
        boolean client = entity.getLevel().isClientSide();
        MolangRuntime.Builder builder = MolangRuntime.runtime();
        AnimationRuntime.addEntity(builder, entity, client);
        return createState(states, builder, client);
    }
}
