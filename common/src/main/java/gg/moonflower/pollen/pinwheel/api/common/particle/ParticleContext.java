package gg.moonflower.pollen.pinwheel.api.common.particle;

import gg.moonflower.pollen.pinwheel.api.common.particle.event.ParticleEvent;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

import java.util.Random;

/**
 * Basic context from a particle for {@link ParticleEvent}.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public interface ParticleContext {

    /**
     * Spawns a particle effect.
     *
     * @param effect The effect to spawn
     * @param type   The way to spawn the particle
     */
    void particleEffect(String effect, ParticleEvent.ParticleSpawnType type);

    /**
     * Plays a sound.
     *
     * @param sound The id of the sound to play
     */
    void soundEffect(ResourceLocation sound);

    /**
     * Executes an expression.
     *
     * @param expression The expression to execute
     */
    void expression(MolangExpression expression);

    /**
     * Logs a message to chat.
     *
     * @param message The message to send
     */
    void log(String message);

    /**
     * @return The source of random
     */
    RandomSource getRandom();
}
