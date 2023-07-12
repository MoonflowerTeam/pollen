package gg.moonflower.pollen.api.render.particle.v1;

import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleEmitterListener;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public interface BedrockParticleEmitter extends BedrockParticle {

    /**
     * Adds the specified listener to the emitter list.
     *
     * @param listener The listener to add
     */
    void addEmitterListener(BedrockParticleEmitterListener listener);

    /**
     * Removes the specified listener from the emitter list.
     *
     * @param listener The listener to remove
     */
    void removeEmitterListener(BedrockParticleEmitterListener listener);

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
     * @return Creates a new particle that can be summoned with {@link #summonParticle(BedrockParticle, double, double, double)}
     */
    BedrockParticle newParticle();

    /**
     * Summons a particle relative to this particle.
     *
     * @param particle The particle to add
     * @param x        The x position relative to this particle to summon
     * @param y        The y position relative to this particle to summon
     * @param z        The z position relative to this particle to summon
     */
    void summonParticle(BedrockParticle particle, double x, double y, double z);

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

    /**
     * @return The entity this emitter is attached to or <code>null</code> if freestanding
     */
    @Nullable
    Entity getEntity();
}
