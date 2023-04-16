package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.EmitterLifetimeExpressionComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
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
        this.particle.setActive(this.data.activation().safeResolve(environment) != 0);
        if (this.data.expiration().safeResolve(environment) != 0) {
            this.particle.expire();
        }
    }
}
