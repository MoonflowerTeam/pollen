package gg.moonflower.pollen.api.animation.v1;

import gg.moonflower.pollen.api.animation.v1.controller.StateAnimationController;
import gg.moonflower.pollen.api.animation.v1.state.AnimationState;
import gg.moonflower.pollen.impl.animation.runtime.AnimationRuntimeImpl;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.world.entity.Entity;

/**
 * Manages the creation of MoLang runtime environments for Minecraft.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface AnimationRuntime {

    /**
     * Adds global MoLang parameters to the specified builder.
     *
     * @param builder The builder to add to
     */
    static void addGlobal(MolangRuntime.Builder builder) {
        AnimationRuntimeImpl.addGlobal(builder);
    }

    /**
     * Adds entity-specific MoLang parameters to the specified builder.
     *
     * @param builder The builder to add to
     */
    static void addEntity(MolangRuntime.Builder builder, Entity entity, boolean client) {
        AnimationRuntimeImpl.addEntity(builder, entity, client);
    }

    /**
     * Creates a state animation controller.
     *
     * @param states  The states to have
     * @param builder The MoLang builder to use
     * @param client  Whether the controller is client-sided
     * @return A new state animation controller
     */
    static StateAnimationController createState(AnimationState[] states, MolangRuntime.Builder builder, boolean client) {
        return AnimationRuntimeImpl.createStateController(states, builder, client);
    }

    /**
     * Creates a state animation controller.
     *
     * @param states The states to have
     * @param entity The entity to create a state controller for
     * @return A new state animation controller
     */
    static StateAnimationController createState(AnimationState[] states, Entity entity) {
        boolean client = entity.getLevel().isClientSide();
        MolangRuntime.Builder builder = MolangRuntime.runtime();
        AnimationRuntime.addEntity(builder, entity, client);
        return createState(states, builder, client);
    }
}
