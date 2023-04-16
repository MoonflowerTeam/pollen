package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.EmitterLifetimeOnceComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EmitterLifetimeOnceComponentImpl extends BedrockParticleEmitterComponentImpl implements BedrockParticleTickComponent, BedrockParticleListener {

    private final EmitterLifetimeOnceComponent data;
    private int activeTimeEval;

    public EmitterLifetimeOnceComponentImpl(BedrockParticle particle, EmitterLifetimeOnceComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void tick() {
        this.particle.setLifetime(this.activeTimeEval);
        if (!this.particle.isActive()) {
            this.particle.expire();
        }
    }

    @Override
    public void onCreate(BedrockParticle particle) {
        this.activeTimeEval = (int) this.data.activeTime().safeResolve(particle.getEnvironment());
    }
}
