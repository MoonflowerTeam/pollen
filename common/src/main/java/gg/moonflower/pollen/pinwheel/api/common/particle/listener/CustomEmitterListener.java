package gg.moonflower.pollen.pinwheel.api.common.particle.listener;

import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;

/**
 * Listens for special events in {@link CustomParticleEmitter}.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public interface CustomEmitterListener {

    /**
     * Spawns the specified number of particles.
     *
     * @param count The amount of particles spawned
     */
    default void onEmitParticles(CustomParticleEmitter emitter, int count) {
    }

    /**
     * Called when the specified emitter restarts.
     *
     * @param emitter The emitter to loop
     */
    default void onLoop(CustomParticleEmitter emitter) {
    }
}
