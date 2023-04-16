package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.ParticleMotionDynamicComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysicsComponent;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
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
        MolangEnvironment runtime = this.particle.getEnvironment();
        Vector3dc acceleration = physics.getAcceleration();
        float accelerationX = (float) (acceleration.x() + this.data.linearAcceleration()[0].safeResolve(runtime)) / 400F; // 400 because 20 * 20 and the units need to be blocks/tick/tick
        float accelerationY = (float) (acceleration.y() + this.data.linearAcceleration()[1].safeResolve(runtime)) / 400F;
        float accelerationZ = (float) (acceleration.z() + this.data.linearAcceleration()[2].safeResolve(runtime)) / 400F;
        float drag = this.data.linearDragCoefficient().safeResolve(runtime) / 400F;
        Vector3dc velocity = physics.getVelocity();
        physics.setAcceleration(this.acceleration.set(accelerationX - drag * velocity.x(), accelerationY - drag * velocity.y(), accelerationZ - drag * velocity.z()));

        float rotationAcceleration = physics.getRotationAcceleration() + this.data.rotationAcceleration().safeResolve(runtime) / 400F;
        float rotationDrag = this.data.rotationDragCoefficient().safeResolve(runtime) / 400F;
        physics.setRotationAcceleration(rotationAcceleration - rotationDrag * physics.getRotationVelocity());
    }
}
