package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.ParticleInitialSpeedComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
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
        float dx = this.data.speed()[0].safeResolve(environment) / 20F;
        float dy = this.data.speed()[1].safeResolve(environment) / 20F;
        float dz = this.data.speed()[2].safeResolve(environment) / 20F;
        physics.setVelocity(physics.getDirection().mul(dx, dy, dz, new Vector3d()));
    }
}
