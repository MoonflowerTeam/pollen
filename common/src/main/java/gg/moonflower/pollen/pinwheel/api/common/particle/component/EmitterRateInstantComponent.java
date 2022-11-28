package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomEmitterListener;
import gg.moonflower.pollen.api.particle.PollenParticleComponents;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import io.github.ocelot.molangcompiler.api.MolangExpression;

/**
 * Component that summons particles once.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterRateInstantComponent implements CustomParticleEmitterComponent, CustomEmitterListener {

    private final MolangExpression particleCount;
    private boolean complete;

    public EmitterRateInstantComponent(JsonElement json) throws JsonParseException {
        this.particleCount = JSONTupleParser.getExpression(json.getAsJsonObject(), "num_particles", () -> MolangExpression.of(10));
    }

    @Override
    public void tick(CustomParticleEmitter emitter) {
        if (this.complete)
            return;
        emitter.emitParticles((int) this.particleCount.safeResolve(emitter.getRuntime()));
    }

    @Override
    public void onLoop(CustomParticleEmitter emitter) {
        this.complete = false;
    }

    @Override
    public CustomParticleComponentType<?> type() {
        return PollenParticleComponents.EMITTER_RATE_INSTANT.get();
    }
}
