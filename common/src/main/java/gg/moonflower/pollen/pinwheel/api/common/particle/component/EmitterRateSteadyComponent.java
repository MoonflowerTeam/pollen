package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.particle.PollenParticleComponents;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomEmitterListener;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomParticleListener;
import io.github.ocelot.molangcompiler.api.MolangExpression;

/**
 * Component that summons particles once.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterRateSteadyComponent implements CustomParticleEmitterComponent, CustomParticleListener, CustomEmitterListener {

    private final MolangExpression spawnRate;
    private final MolangExpression maxParticles;
    private int maxParticlesEval;

    public EmitterRateSteadyComponent(JsonElement json) throws JsonParseException {
        this.spawnRate = JSONTupleParser.getExpression(json.getAsJsonObject(), "spawn_rate", () -> MolangExpression.of(1));
        this.maxParticles = JSONTupleParser.getExpression(json.getAsJsonObject(), "max_particles", () -> MolangExpression.of(50));
    }

    @Override
    public void tick(CustomParticleEmitter emitter) {
        int spawnCount = (int) (this.spawnRate.safeResolve(emitter.getRuntime()) / 20F);
        for (int i = 0; i < spawnCount; i++) {
            if (emitter.getSpawnedParticles() >= this.maxParticlesEval)
                break;
            emitter.emitParticles(1);
            spawnCount = (int) (this.spawnRate.safeResolve(emitter.getRuntime()) / 20F);
        }
    }

    @Override
    public void onCreate(CustomParticle particle) {
        this.maxParticlesEval = (int) this.maxParticles.safeResolve(particle.getRuntime());
    }

    @Override
    public void onLoop(CustomParticleEmitter emitter) {
        this.onCreate(emitter);
    }

    @Override
    public CustomParticleComponentType<?> type() {
        return PollenParticleComponents.EMITTER_RATE_STEADY.get();
    }
}
