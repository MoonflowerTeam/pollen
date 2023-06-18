package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.particle.component.ParticleMotionDynamicComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysicsComponent;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@ApiStatus.Internal
public class ParticleMotionDynamicComponentImpl extends BedrockParticleComponentImpl implements BedrockParticlePhysicsComponent {

    private final ParticleMotionDynamicComponent data;
    private final Vector3d acceleration;

    public ParticleMotionDynamicComponentImpl(BedrockParticle particle, ParticleMotionDynamicComponent data) {
        super(particle);
        this.data = data;
        this.acceleration = new Vector3d();
    }

    @Override
    public void physicsTick() {
        BedrockParticlePhysics physics = this.getPhysics();
        MolangEnvironment environment = this.particle.getEnvironment();
        Vector3dc acceleration = physics.getAcceleration();
        float accelerationX = (float) (acceleration.x() + environment.safeResolve(this.data.linearAcceleration()[0])) / 400F; // 400 because 20 * 20 and the units need to be blocks/tick/tick
        float accelerationY = (float) (acceleration.y() + environment.safeResolve(this.data.linearAcceleration()[1])) / 400F;
        float accelerationZ = (float) (acceleration.z() + environment.safeResolve(this.data.linearAcceleration()[2])) / 400F;
        float drag = environment.safeResolve(this.data.linearDragCoefficient()) / 400F;
        Vector3dc velocity = physics.getVelocity();
        physics.setAcceleration(this.acceleration.set(accelerationX - drag * velocity.x(), accelerationY - drag * velocity.y(), accelerationZ - drag * velocity.z()));

        float rotationAcceleration = physics.getRotationAcceleration() + environment.safeResolve(this.data.rotationAcceleration()) / 400F;
        float rotationDrag = environment.safeResolve(this.data.rotationDragCoefficient()) / 400F;
        physics.setRotationAcceleration(rotationAcceleration - rotationDrag * physics.getRotationVelocity());
    }
}
