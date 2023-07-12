package gg.moonflower.pollen.api.render.particle.v1.listener;

import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;

/**
 * Listens for lifecycle events in {@link BedrockParticle}.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public interface BedrockParticleListener {

    /**
     * Called when the specified particle is created.
     *
     * @param particle The added particle
     */
    default void onCreate(BedrockParticle particle) {
    }

    /**
     * Called when the specified particle is removed.
     *
     * @param particle The removed particle
     */
    default void onExpire(BedrockParticle particle) {
    }

    /**
     * Called when the particle collides with a block.
     *
     * @param particle The particle colliding
     * @param x        If the collision was in the x axis
     * @param y        If the collision was in the y axis
     * @param z        If the collision was in the z axis
     */
    default void onCollide(BedrockParticle particle, boolean x, boolean y, boolean z) {
    }

    /**
     * Called when the particle moves.
     *
     * @param particle The particle to move
     * @param dx       The offset in the x
     * @param dy       The offset in the y
     * @param dz       The offset in the z
     */
    default void onMove(BedrockParticle particle, double dx, double dy, double dz) {
    }
}
