package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.particle.PollenParticleComponents;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomParticleListener;
import io.github.ocelot.molangcompiler.api.MolangExpression;

/**
 * Component that initializes emitters.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterInitializationComponent implements CustomParticleEmitterComponent, CustomParticleListener {

    private final MolangExpression creationExpression;
    private final MolangExpression tickExpression;

    public EmitterInitializationComponent(JsonElement json) throws JsonParseException {
        this.creationExpression = JSONTupleParser.getExpression(json.getAsJsonObject(), "creation_expression", () -> null);
        this.tickExpression = JSONTupleParser.getExpression(json.getAsJsonObject(), "per_update_expression", () -> null);
    }

    @Override
    public void tick(CustomParticleEmitter emitter) {
        if (this.tickExpression != null)
            this.tickExpression.safeResolve(emitter.getRuntime());
    }

    @Override
    public void onCreate(CustomParticle particle) {
        if (this.creationExpression != null)
            this.creationExpression.safeResolve(particle.getRuntime());
    }

    @Override
    public CustomParticleComponentType<?> type() {
        return PollenParticleComponents.EMITTER_INITIALIZATION.get();
    }
}
