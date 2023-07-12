package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.ParticleMotionCollisionComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysicsComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;

@ApiStatus.Internal
public class ParticleMotionCollisionComponentImpl extends BedrockParticleComponentImpl implements BedrockParticlePhysicsComponent, BedrockParticleListener {

    private final ParticleMotionCollisionComponent data;
    private final Vector3d acceleration;
    private final Vector3d velocity;

    public ParticleMotionCollisionComponentImpl(BedrockParticle particle, ParticleMotionCollisionComponent data) {
        super(particle);
        this.data = data;
        this.acceleration = new Vector3d();
        this.velocity = new Vector3d();
    }

    @Override
    public void physicsTick() {
        this.getPhysics().setCollision(this.particle.getEnvironment().safeResolve(this.data.enabled()) == 1);
    }

    @Override
    public void onCreate(BedrockParticle particle) {
        this.getPhysics().setCollisionRadius(this.data.collisionRadius());
    }

    @Override
    public void onCollide(BedrockParticle particle, boolean x, boolean y, boolean z) {
        BedrockParticlePhysics physics = this.getPhysics();
        physics.setSpeed(physics.getSpeed() - this.data.collisionDrag());
        if (y) {
            physics.setAcceleration(physics.getAcceleration().mul(1, -this.data.coefficientOfRestitution(), 1, this.acceleration));
            physics.setVelocity(physics.getVelocity().mul(1, -this.data.coefficientOfRestitution(), 1, this.velocity));
        }
        if (this.data.expireOnContact()) {
            particle.expire();
        }
        for (String event : this.data.events()) {
            particle.runEvent(event);
        }
    }
}
