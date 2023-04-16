package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.EmitterRateInstantComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleEmitterListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EmitterRateInstantComponentImpl extends BedrockParticleEmitterComponentImpl implements BedrockParticleTickComponent, BedrockParticleEmitterListener {

    private final EmitterRateInstantComponent data;
    private boolean complete;

    public EmitterRateInstantComponentImpl(BedrockParticle particle, EmitterRateInstantComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void tick() {
        if (!this.complete) {
            this.particle.emitParticles((int) this.data.particleCount().safeResolve(this.particle.getEnvironment()));
        }
    }

    @Override
    public void onLoop(BedrockParticleEmitter emitter) {
        this.complete = false;
    }
}
