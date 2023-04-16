package gg.moonflower.pollen.api.render.particle.v1.listener;

import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;

/**
 * Listens for special events in {@link BedrockParticleEmitter}.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface BedrockParticleEmitterListener {

    /**
     * Spawns the specified number of particles.
     *
     * @param count The amount of particles spawned
     */
    default void onEmitParticles(BedrockParticleEmitter emitter, int count) {
    }

    /**
     * Called when the specified emitter restarts.
     *
     * @param emitter The emitter to loop
     */
    default void onLoop(BedrockParticleEmitter emitter) {
    }
}
