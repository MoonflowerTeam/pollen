package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.particle.component.EmitterLifetimeExpressionComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EmitterLifetimeExpressionComponentImpl extends BedrockParticleEmitterComponentImpl implements BedrockParticleTickComponent {

    private final EmitterLifetimeExpressionComponent data;

    public EmitterLifetimeExpressionComponentImpl(BedrockParticle particle, EmitterLifetimeExpressionComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void tick() {
        MolangEnvironment environment = this.particle.getEnvironment();
        this.particle.setActive(environment.safeResolve(this.data.activation()) != 0);
        if (environment.safeResolve(this.data.expiration()) != 0) {
            this.particle.expire();
        }
    }
}
