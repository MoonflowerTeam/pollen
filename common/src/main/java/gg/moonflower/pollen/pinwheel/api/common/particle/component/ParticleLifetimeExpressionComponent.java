package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomParticleListener;
import io.github.ocelot.molangcompiler.api.MolangExpression;

/**
 * Component that controls when a particle should be removed and how long it can live for.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleLifetimeExpressionComponent implements CustomParticleComponent, CustomParticleListener {

    private final MolangExpression expirationExpression;
    private final MolangExpression maxLifetime;

    public ParticleLifetimeExpressionComponent(JsonElement json) throws JsonParseException {
        this.expirationExpression = JSONTupleParser.getExpression(json.getAsJsonObject(), "expiration_expression", () -> MolangExpression.ZERO);
        this.maxLifetime = JSONTupleParser.getExpression(json.getAsJsonObject(), "max_lifetime", () -> MolangExpression.of(1));
    }

    @Override
    public void tick(CustomParticle particle) {
        if (this.expirationExpression.safeResolve(particle.getRuntime()) != 0)
            particle.expire();
    }

    @Override
    public void onCreate(CustomParticle particle) {
        particle.setLifetime(this.maxLifetime.safeResolve(particle.getRuntime()));
    }
}
