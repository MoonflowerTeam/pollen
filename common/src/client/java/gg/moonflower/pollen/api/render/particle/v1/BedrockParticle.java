package gg.moonflower.pollen.api.render.particle.v1;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import gg.moonflower.pinwheel.api.particle.ParticleContext;
import gg.moonflower.pinwheel.api.particle.ParticleInstance;
import gg.moonflower.pinwheel.api.particle.render.ParticleRenderProperties;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import gg.moonflower.pollen.impl.particle.BedrockParticleOption;
import gg.moonflower.pollen.impl.particle.PollenParticles;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

/**
 * An instance of a bedrock modular particle
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface BedrockParticle extends ParticleInstance, ParticleContext {

    /**
     * Retrieves the particle options for the specified particle name. This allows custom particles to also function
     *
     * @param effect The effect to parse
     * @return The options to spawn that particle
     * @throws CommandSyntaxException If the effect is vanilla and fails to parse
     */
    static ParticleOptions getOptions(String effect) throws CommandSyntaxException {
        ResourceLocation id = ResourceLocation.tryParse(effect);
        if (id != null && BedrockParticleManager.hasParticle(id)) {
            return new BedrockParticleOption(PollenParticles.CUSTOM.get(), id);
        }

        return ParticleArgument.readParticle(new StringReader(effect));
    }

    /**
     * Adds the specified listener to the listener list.
     *
     * @param listener The listener to add
     */
    void addListener(BedrockParticleListener listener);

    /**
     * Removes the specified listener from the listener list.
     *
     * @param listener The listener to remove
     */
    void removeListener(BedrockParticleListener listener);

    /**
     * Runs the specified event if it exists.
     *
     * @param name The event to run
     */
    void runEvent(String name);

    @Override
    default void expression(MolangExpression expression) {
        expression.safeResolve(this.getEnvironment());
    }

    /**
     * @return The position of this particle
     */
    Vector3dc position();

    /**
     * @return The position of this particle aligned to the block grid
     */
    BlockPos blockPosition();

    /**
     * @return The roll of this particle
     */
    float roll();

    /**
     * @return The render position of this particle
     */
    Vector3dc position(float partialTicks);

    /**
     * @return The roll of this particle
     */
    float roll(float partialTicks);

    /**
     * Removes this particle and triggers listeners.
     */
    void expire();

    /**
     * @return If this particle is scheduled to be removed
     */
    boolean isExpired();

    /**
     * @return The name of this particle
     */
    ResourceLocation getName();

    /**
     * @return The level this particle is in
     */
    Level getLevel();

    /**
     * Retrieves the light UV at this particle's position.
     *
     * @return The packed lightmap coordinates
     */
    default int getPackedLight() {
        Level level = this.getLevel();
        BlockPos blockPos = this.blockPosition();
        return level.hasChunkAt(blockPos) ? LevelRenderer.getLightColor(level, blockPos) : 0;
    }

    /**
     * @return The physics of this particle or <code>null</code> if there are none
     */
    @Nullable BedrockParticlePhysics getPhysics();

    /**
     * @return The properties to use when rendering or <code>null</code> if nothing is rendered
     */
    @Nullable ParticleRenderProperties getRenderProperties();

    /**
     * @return The emitter source this particle came from, or this particle if already an emitter
     */
    BedrockParticleEmitter getEmitter();

    /**
     * Sets the amount of time an emitter should spawn particles for or how long a particle should live.
     *
     * @param time The duration to spawn in seconds
     */
    void setLifetime(float time);

    /**
     * Sets whether particles will be spawned.
     *
     * @param active If particles should spawn
     */
    default void setActive(boolean active) {
        this.setLifetime(active ? Float.MAX_VALUE : 0);
    }

    /**
     * Sets the x position of this particle.
     *
     * @param x The new x value
     */
    void setX(double x);

    /**
     * Sets the y position of this particle.
     *
     * @param y The new y value
     */
    void setY(double y);

    /**
     * Sets the z position of this particle.
     *
     * @param z The new z value
     */
    void setZ(double z);

    /**
     * Sets the position of this particle.
     *
     * @param x The new x value
     * @param y The new y value
     * @param z The new z value
     */
    void setPosition(double x, double y, double z);

    /**
     * Sets the position of this particle.
     *
     * @param pos The new position
     */
    default void setPosition(Vector3dc pos) {
        this.setPosition(pos.x(), pos.y(), pos.z());
    }

    /**
     * Sets the roll of this particle.
     *
     * @param roll The new roll value
     */
    void setRoll(float roll);

    /**
     * Sets the render properties for this particle. Only works if this is a particle.
     *
     * @param properties The new properties to use when rendering or <code>null</code> to draw nothing
     */
    void setRenderProperties(@Nullable ParticleRenderProperties properties);
}
