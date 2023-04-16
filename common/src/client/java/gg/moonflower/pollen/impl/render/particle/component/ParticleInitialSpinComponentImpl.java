package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.ParticleInitialSpinComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysicsComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ParticleInitialSpinComponentImpl extends BedrockParticleComponentImpl implements BedrockParticleListener, BedrockParticlePhysicsComponent {

    private final ParticleInitialSpinComponent data;

    public ParticleInitialSpinComponentImpl(BedrockParticle particle, ParticleInitialSpinComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void onCreate(BedrockParticle particle) {
        BedrockParticlePhysics physics = this.getPhysics();
        MolangEnvironment environment = particle.getEnvironment();
        particle.setRoll(this.data.rotation().safeResolve(environment));
        physics.setRollVeclocity(this.data.rotationRate().safeResolve(environment) / 20F);
    }

    @Override
    public void physicsTick() {
    }
}
