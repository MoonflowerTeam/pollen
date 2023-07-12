package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleComponent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public abstract class BedrockParticleEmitterComponentImpl implements BedrockParticleComponent {

    protected final BedrockParticleEmitter particle;

    protected BedrockParticleEmitterComponentImpl(BedrockParticle particle) {
        if (!(particle instanceof BedrockParticleEmitter emitter)) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " can only be added to particle emitters");
        }
        this.particle = emitter;
    }
}
