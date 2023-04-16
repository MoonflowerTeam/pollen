package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.ParticleLifetimeExpressionComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ParticleLifetimeExpressionComponentImpl extends BedrockParticleComponentImpl implements BedrockParticleTickComponent, BedrockParticleListener {

    private final ParticleLifetimeExpressionComponent data;

    public ParticleLifetimeExpressionComponentImpl(BedrockParticle particle, ParticleLifetimeExpressionComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void tick() {
        if (this.data.expirationExpression().safeResolve(this.particle.getEnvironment()) != 0) {
            this.particle.expire();
        }
    }

    @Override
    public void onCreate(BedrockParticle particle) {
        particle.setLifetime(this.data.maxLifetime().safeResolve(particle.getEnvironment()));
    }
}
