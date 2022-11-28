package gg.moonflower.pollen.pinwheel.api.common.particle.listener;

import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import net.minecraft.core.Direction;

/**
 * Listens for lifecycle events in {@link CustomParticle}.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public interface CustomParticleListener {

    /**
     * Called when the specified particle is created.
     *
     * @param particle The added particle
     */
    default void onCreate(CustomParticle particle) {
    }

    /**
     * Called when the specified particle is removed.
     *
     * @param particle The removed particle
     */
    default void onExpire(CustomParticle particle) {
    }

    /**
     * Called each tick to update the amount of time the particle has been alive.
     *
     * @param particle The ticking particle
     * @param time     The time alive in seconds
     */
    default void onTimeline(CustomParticle particle, float time) {
    }

    /**
     * Called when the particle collides with a block.
     *
     * @param particle The particle colliding
     * @param x If the collision was in the x axis
     * @param y If the collision was in the y axis
     * @param z If the collision was in the z axis
     */
    default void onCollide(CustomParticle particle, boolean x, boolean y, boolean z) {
    }

    /**
     * Called when the particle moves.
     *
     * @param particle The particle to move
     * @param oldX     The x position before moving
     * @param oldY     The y position before moving
     * @param oldZ     The z position before moving
     * @param x        The current x position
     * @param y        The current y position
     * @param z        The current z position
     */
    default void onMove(CustomParticle particle, double oldX, double oldY, double oldZ, double x, double y, double z) {
    }
}
