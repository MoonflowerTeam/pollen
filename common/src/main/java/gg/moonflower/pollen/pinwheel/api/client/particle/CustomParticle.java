package gg.moonflower.pollen.pinwheel.api.client.particle;

import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomParticleListener;
import gg.moonflower.pollen.pinwheel.api.common.particle.render.CustomParticleRenderProperties;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

/**
 * A custom particle loaded from {@link CustomParticleManager}.
 *
 * @author Ocelot
 * @see CustomParticleManager
 * @since 1.6.0
 */
public interface CustomParticle {

    /**
     * Adds the specified listener to the listener list.
     *
     * @param listener The listener to add
     */
    void addListener(CustomParticleListener listener);

    /**
     * Removes the specified listener from the listener list.
     *
     * @param listener The listener to remove
     */
    void removeListener(CustomParticleListener listener);

    /**
     * Runs the specified event if it exists.
     *
     * @param name The event to run
     */
    void runEvent(String name);

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
     * Removes this particle and triggers listeners.
     */
    void expire();

    /**
     * @return If this particle is scheduled to be removed
     */
    boolean isExpired();

    /**
     * @return How old this particle is in seconds
     */
    float getParticleAge();

    /**
     * @return The amount of time until this particle expires or loops in seconds
     */
    float getParticleLifetime();

    /**
     * @return The name of this particle
     */
    ResourceLocation getName();

    /**
     * @return The runtime used to evaluate expression
     */
    MolangRuntime getRuntime();

    /**
     * @return The level this particle is in
     */
    Level getLevel();

    /**
     * @return The position of this particle aligned to the block grid
     */
    BlockPos blockPos();

    /**
     * Retrieves the light UV at this particle's position.
     *
     * @return The packed lightmap coordinates
     */
    default int getPackedLight() {
        Level level = this.getLevel();
        BlockPos blockPos = this.blockPos();
        return level.hasChunkAt(blockPos) ? LevelRenderer.getLightColor(level, blockPos) : 0;
    }

    /**
     * @return The x position of the particle
     */
    double x();

    /**
     * @return The y position of the particle
     */
    double y();

    /**
     * @return The z position of the particle
     */
    double z();

    /**
     * @return The roll of this particle
     */
    float rotation();

    /**
     * @return The normalized direction this particle is travelling
     */
    Vec3 getDirection();

    /**
     * @return The speed of motion
     */
    float getSpeed();

    /**
     * @return The current velocity of this particle in blocks/tick
     */
    default Vec3 getVelocity() {
        float speed = this.getSpeed();
        return this.getDirection().multiply(speed, speed, speed);
    }

    /**
     * @return The cuurrent acceleration of this particle in blocks/tick/tick
     */
    Vec3 getAcceleration();

    /**
     * @return The velocity of rotation in degrees/tick
     */
    float getRotationVelocity();

    /**
     * @return The acceleration of rotation in degrees/tick/tick
     */
    float getRotationAcceleration();

    /**
     * @return The particle random instance
     */
    Random getRandom();

    /**
     * @return The properties to use when rendering or <code>null</code> if nothing is rendered
     */
    @Nullable
    CustomParticleRenderProperties getRenderProperties();

    /**
     * @return Whether this particle can collide with the environment
     */
    boolean hasCollision();

    /**
     * @return The radis of the box used to calculate collisions
     */
    float getCollisionRadius();

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

    void setX(double x);

    void setY(double y);

    void setZ(double z);

    default void setPos(double x, double y, double z) {
        this.setX(x);
        this.setY(y);
        this.setZ(z);
    }

    /**
     * Sets the roll of this particle.
     *
     * @param rotation The new roll value
     */
    void setRotation(float rotation);

    /**
     * Sets the velocity of this particle in blocks/tick
     *
     * @param velocity The new velocity
     */
    default void setVelocity(Vec3 velocity) {
        this.setDirection(velocity);
        this.setSpeed((float) velocity.length());
    }

    /**
     * Sets the direction this particle will travel in.
     *
     * @param direction The direction to travel
     */
    void setDirection(Vec3 direction);

    /**
     * Sets the speed of this particle.
     *
     * @param speed The speed to set the particle to
     */
    void setSpeed(float speed);

    /**
     * Sets the acceleration of this particle in blocks/tick/tick
     *
     * @param acceleration The new acceleration
     */
    void setAcceleration(Vec3 acceleration);

    /**
     * Sets how fast this particle will roll in degrees/tick.
     *
     * @param velocity The velocity of roll
     */
    void setRotationVelocity(float velocity);

    /**
     * Sets how fast this particle will accelerate rolling in degrees/tick/tick.
     *
     * @param acceleration The acceleration of roll
     */
    void setRotationAcceleration(float acceleration);

    /**
     * Sets the render properties for this particle. Only works if {@link #isParticle()} is <code>true</code>.
     *
     * @param properties The new properties to use when rendering or <code>null</code> to draw nothing
     */
    void setRenderProperties(@Nullable CustomParticleRenderProperties properties);

    /**
     * Sets whether collisions will be calculated for this particle.
     *
     * @param enabled Whether to calculate collisions
     */
    void setCollision(boolean enabled);

    /**
     * Sets the size of the bounding box on this particle. Only applies if {@link #hasCollision()} is <code>true</code>.
     *
     * @param radius The radius of the box used to calculate collisions.
     */
    void setCollisionRadius(float radius);

    /**
     * @return If this particle is an actual particle instead of an emitter
     */
    boolean isParticle();
}
