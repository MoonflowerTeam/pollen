package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.EmitterInitializationComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleComponent;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ParticleInitializationComponentImpl implements BedrockParticleComponent, BedrockParticleTickComponent, BedrockParticleListener {

    private final BedrockParticle particle;
    private final EmitterInitializationComponent data;

    public ParticleInitializationComponentImpl(BedrockParticle particle, EmitterInitializationComponent data) {
        this.particle = particle;
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
