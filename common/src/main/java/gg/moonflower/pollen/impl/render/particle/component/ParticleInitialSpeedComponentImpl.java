package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.particle.component.ParticleInitialSpeedComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;

@ApiStatus.Internal
public class ParticleInitialSpeedComponentImpl extends BedrockParticleComponentImpl implements BedrockParticleListener {

    private final ParticleInitialSpeedComponent data;

    public ParticleInitialSpeedComponentImpl(BedrockParticle particle, ParticleInitialSpeedComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void onCreate(BedrockParticle particle) {
        BedrockParticlePhysics physics = this.getPhysics();
        MolangEnvironment environment = particle.getEnvironment();
        float dx = environment.safeResolve(this.data.speed()[0]) / 20F;
        float dy = environment.safeResolve(this.data.speed()[1]) / 20F;
        float dz = environment.safeResolve(this.data.speed()[2]) / 20F;
        physics.setVelocity(physics.getDirection().mul(dx, dy, dz, new Vector3d()));
    }
}
