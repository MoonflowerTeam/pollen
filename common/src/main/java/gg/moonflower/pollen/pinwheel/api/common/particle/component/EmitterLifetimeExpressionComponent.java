package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import io.github.ocelot.molangcompiler.api.MolangExpression;

/**
 * Component that controls when an emitter can produce particles and if it should be removed.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterLifetimeExpressionComponent implements CustomParticleEmitterComponent {

    private final MolangExpression activationExpression;
    private final MolangExpression expirationExpression;

    public EmitterLifetimeExpressionComponent(JsonElement json) throws JsonParseException {
        this.activationExpression = JSONTupleParser.getExpression(json.getAsJsonObject(), "activation_expression", () -> MolangExpression.of(1));
        this.expirationExpression = JSONTupleParser.getExpression(json.getAsJsonObject(), "expiration_expression", () -> MolangExpression.ZERO);
    }

    @Override
    public void tick(CustomParticleEmitter emitter) {
        emitter.setActive(this.activationExpression.safeResolve(emitter.getRuntime()) != 0);
        if (this.expirationExpression.safeResolve(emitter.getRuntime()) != 0)
            emitter.expire();
    }
}
