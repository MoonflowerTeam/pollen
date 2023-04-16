package gg.moonflower.pollen.impl.render.particle;

import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@ApiStatus.Internal
public class BedrockParticlePhysicsImpl implements BedrockParticlePhysics {

    private final Vector3d direction;
    private float speed;
    private final Vector3d velocity;
    private final Vector3d acceleration;

    private float rotationVelocity;
    private float rotationAcceleration;
    private float collisionRadius;
    private boolean collision;

    public BedrockParticlePhysicsImpl() {
        this.direction = new Vector3d();
        this.speed = 0;
        this.velocity = new Vector3d();
        this.acceleration = new Vector3d();

        this.rotationVelocity = 0;
        this.rotationAcceleration = 0;
        this.collisionRadius = 0.1F;
        this.collision = true;
    }

    public void tick(){
        if (this.acceleration.lengthSquared() > 1.0E-7) {
            this.setVelocity(this.getVelocity().add(this.acceleration, new Vector3d()));
        }
    }

    @Override
    public Vector3dc getDirection() {
        return this.direction;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public Vector3dc getVelocity() {
        return this.direction.mul(this.speed, this.velocity);
    }

    @Override
    public Vector3dc getAcceleration() {
        return this.acceleration;
    }

    @Override
    public float getRotationVelocity() {
        return this.rotationVelocity;
    }

    @Override
    public float getRotationAcceleration() {
        return this.rotationAcceleration;
    }

    @Override
    public boolean hasCollision() {
        return this.collision;
    }

    @Override
    public float getCollisionRadius() {
        return this.collisionRadius;
    }

    @Override
    public void setDirection(double dx, double dy, double dz) {
        this.direction.set(dx, dy, dz).normalize();
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public void setAcceleration(Vector3dc acceleration) {
        this.acceleration.set(acceleration);
    }

    @Override
    public void setRollVeclocity(float velocity) {
        this.rotationVelocity = velocity;
    }

    @Override
    public void setRotationAcceleration(float acceleration) {
        this.rotationAcceleration = acceleration;
    }

    @Override
    public void setCollision(boolean enabled) {
        this.collision = enabled;
    }

    @Override
    public void setCollisionRadius(float radius) {
        this.collisionRadius = radius;
    }
}
