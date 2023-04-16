package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.EmitterLifetimeLoopingComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EmitterLifetimeLoopingComponentImpl extends BedrockParticleEmitterComponentImpl implements BedrockParticleTickComponent, BedrockParticleListener {

    private final EmitterLifetimeLoopingComponent data;
    private int activeTimeEval;
    private int sleepTimer;

    public EmitterLifetimeLoopingComponentImpl(BedrockParticle particle, EmitterLifetimeLoopingComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void tick() {
        this.particle.setLifetime(this.activeTimeEval);
        if (!this.particle.isActive()) {
            if (this.sleepTimer > 0) { // Wait for sleep to complete
                this.sleepTimer--;
            } else {
                this.particle.restart();
            }
        }
    }

    @Override
    public void onCreate(BedrockParticle particle) {
        MolangEnvironment runtime = particle.getEnvironment();
        this.activeTimeEval = (int) this.data.activeTime().safeResolve(runtime);
        this.sleepTimer = (int) this.data.sleepTime().safeResolve(runtime);
    }
}
