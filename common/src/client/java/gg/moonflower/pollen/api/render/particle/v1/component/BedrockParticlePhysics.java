package gg.moonflower.pollen.api.render.particle.v1.component;

import org.joml.Vector3dc;

public interface BedrockParticlePhysics {

    /**
     * @return The normalized direction this particle is travelling
     */
    Vector3dc getDirection();

    /**
     * @return The speed of motion
     */
    float getSpeed();

    /**
     * @return The square of the speed of motion
     */
    default float getSquareSpeed() {
        float speed = this.getSpeed();
        return speed * speed;
    }

    /**
     * @return The current velocity of this particle in blocks/tick
     */
    Vector3dc getVelocity();

    /**
     * @return The cuurrent acceleration of this particle in blocks/tick/tick
     */
    Vector3dc getAcceleration();

    /**
     * @return The velocity of rotation in degrees/tick
     */
    float getRotationVelocity();

    /**
     * @return The acceleration of rotation in degrees/tick/tick
     */
    float getRotationAcceleration();

    /**
     * @return Whether this particle can collide with the environment
     */
    boolean hasCollision();

    /**
     * @return The radis of the box used to calculate collisions
     */
    float getCollisionRadius();

    /**
     * Sets the velocity of this particle in blocks/tick
     *
     * @param velocity The new velocity
     */
    default void setVelocity(Vector3dc velocity) {
        this.setDirection(velocity);
        this.setSpeed((float) velocity.length());
    }

    /**
     * Sets the direction this particle will travel in.
     *
     * @param direction The direction to travel
     */
    default void setDirection(Vector3dc direction) {
        this.setDirection(direction.x(), direction.y(), direction.z());
    }

    /**
     * Sets the direction this particle will travel in.
     *
     * @param dx The direction to travel in the x
     * @param dy The direction to travel in the y
     * @param dz The direction to travel in the z
     */
    void setDirection(double dx, double dy, double dz);

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
    void setAcceleration(Vector3dc acceleration);

    /**
     * Sets how fast this particle will roll in degrees/tick.
     *
     * @param velocity The velocity of roll
     */
    void setRollVeclocity(float velocity);

    /**
     * Sets how fast this particle will accelerate rolling in degrees/tick/tick.
     *
     * @param acceleration The acceleration of roll
     */
    void setRotationAcceleration(float acceleration);

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
}
