package gg.moonflower.pollen.pinwheel.api.client.particle;

import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomEmitterListener;
import net.minecraft.core.particles.ParticleOptions;

/**
 * An emitter for {@link CustomParticle}.
 *
 * @author Ocelot
 * @see CustomParticleManager
 * @since 1.6.0
 */
public interface CustomParticleEmitter extends CustomParticle {

    /**
     * Adds the specified listener to the emitter list.
     *
     * @param listener The listener to add
     */
    void addEmitterListener(CustomEmitterListener listener);

    /**
     * Removes the specified listener from the emitter list.
     *
     * @param listener The listener to remove
     */
    void removeEmitterListener(CustomEmitterListener listener);

    /**
     * Spawns the specified number of particles.
     *
     * @param count The amount of particles to spawn
     */
    void emitParticles(int count);

    /**
     * Attempts to restart the emitter if possible. This causes a "loop".
     */
    void restart();

    /**
     * @return Creates a new particle that can be summoned with {@link #summonParticle(ParticleOptions, double, double, double, double, double, double)}
     */
    CustomParticle newParticle();

    /**
     * Summons a particle relative to this particle.
     *
     * @param particle The particle to add
     * @param x        The x position relative to this particle to summon
     * @param y        The y position relative to this particle to summon
     * @param z        The z position relative to this particle to summon
     * @param dx       The motion in the x
     * @param dy       The motion in the y
     * @param dz       The motion in the z
     */
    void summonParticle(CustomParticle particle, double x, double y, double z, double dx, double dy, double dz);

    /**
     * Summons a particle relative to this particle.
     *
     * @param particle The particle to summon
     * @param x        The x position relative to this particle to summon
     * @param y        The y position relative to this particle to summon
     * @param z        The z position relative to this particle to summon
     * @param dx       The motion in the x
     * @param dy       The motion in the y
     * @param dz       The motion in the z
     */
    void summonParticle(ParticleOptions particle, double x, double y, double z, double dx, double dy, double dz);

    /**
     * @return The amount of time to spawn particles for
     */
    default float getActiveTime() {
        return this.getParticleLifetime();
    }

    /**
     * @return Whether particles are currently being spawned
     */
    default boolean isActive() {
        return this.getActiveTime() > this.getParticleAge();
    }

    /**
     * @return The number of particles spawned
     */
    int getSpawnedParticles();
}
