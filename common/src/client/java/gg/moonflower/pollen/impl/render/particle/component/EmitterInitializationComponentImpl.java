package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.EmitterInitializationComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EmitterInitializationComponentImpl extends BedrockParticleEmitterComponentImpl implements BedrockParticleTickComponent, BedrockParticleListener {

    private final EmitterInitializationComponent data;

    public EmitterInitializationComponentImpl(BedrockParticle particle, EmitterInitializationComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void tick() {
        if (this.data.tickExpression() != null) {
            this.particle.getEnvironment().safeResolve(this.data.tickExpression());
        }
    }

    @Override
    public void onCreate(BedrockParticle particle) {
        if (this.data.creationExpression() != null) {
            this.particle.getEnvironment().safeResolve(this.data.creationExpression());
        }
    }
}
