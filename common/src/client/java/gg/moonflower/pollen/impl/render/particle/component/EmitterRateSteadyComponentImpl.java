package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.EmitterRateSteadyComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleEmitterListener;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EmitterRateSteadyComponentImpl extends BedrockParticleEmitterComponentImpl implements BedrockParticleTickComponent, BedrockParticleListener, BedrockParticleEmitterListener {

    private final EmitterRateSteadyComponent data;
    private int maxParticlesEval;

    public EmitterRateSteadyComponentImpl(BedrockParticle particle, EmitterRateSteadyComponent data) {
        super(particle);
        this.data = data;
    }

    private int evaluateSpawnCount() {
        return (int) (this.particle.getEnvironment().safeResolve(this.data.spawnRate()) / 20F);
    }

    @Override
    public void tick() {
        int maxCount = this.maxParticlesEval - this.particle.getSpawnedParticles();
        if (maxCount <= 0) {
            return;
        }

        int spawnCount = this.evaluateSpawnCount();
        for (int i = 0; i < Math.min(spawnCount, maxCount); i++) {
            this.particle.emitParticles(1);
            spawnCount = this.evaluateSpawnCount();
        }
    }

    @Override
    public void onCreate(BedrockParticle particle) {
        this.maxParticlesEval = (int) this.particle.getEnvironment().safeResolve(this.data.maxParticles());
    }

    @Override
    public void onLoop(BedrockParticleEmitter emitter) {
        this.onCreate(emitter);
    }
}
