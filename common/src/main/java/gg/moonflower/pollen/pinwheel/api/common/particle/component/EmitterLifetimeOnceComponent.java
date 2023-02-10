package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import io.github.ocelot.molangcompiler.api.MolangExpression;

/**
 * Component that spawns particles during the active time.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterLifetimeOnceComponent implements CustomParticleComponent, CustomEmitterTickComponent, CustomParticleListener {

    private final MolangExpression activeTime;
    private int activeTimeEval;

    public EmitterLifetimeOnceComponent(JsonElement json) throws JsonParseException {
        this.activeTime = JSONTupleParser.getExpression(json.getAsJsonObject(), "active_time", () -> MolangExpression.of(10));
    }

    @Override
    public void tick(CustomParticleEmitter emitter) {
        emitter.setLifetime(this.activeTimeEval);
        if (!emitter.isActive())
            emitter.expire();
    }

    @Override
    public void onCreate(CustomParticle particle) {
        this.activeTimeEval = (int) this.activeTime.safeResolve(particle.getRuntime());
    }
}
