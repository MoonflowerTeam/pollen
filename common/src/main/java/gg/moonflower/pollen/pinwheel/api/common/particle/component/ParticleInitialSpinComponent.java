package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import gg.moonflower.pollen.api.particle.PollenParticleComponents;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomParticleListener;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;

/**
 * Component that specifies the initial rotation and rotation rate of a particle.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleInitialSpinComponent implements CustomParticleComponent, CustomParticleListener {

    private final MolangExpression rotation;
    private final MolangExpression rotationRate;

    public ParticleInitialSpinComponent(JsonElement json) {
        this.rotation = JSONTupleParser.getExpression(json.getAsJsonObject(), "rotation", () -> MolangExpression.ZERO);
        this.rotationRate = JSONTupleParser.getExpression(json.getAsJsonObject(), "rotation_rate", () -> MolangExpression.ZERO);
    }

    @Override
    public void tick(CustomParticle particle) {
    }

    @Override
    public void onCreate(CustomParticle particle) {
        MolangRuntime runtime = particle.getRuntime();
        particle.setRotation(this.rotation.safeResolve(runtime) / 20F);
        particle.setRotationVelocity(this.rotationRate.safeResolve(runtime) / 20F);
    }

    @Override
    public CustomParticleComponentType<?> type() {
        return PollenParticleComponents.PARTICLE_INITIAL_SPIN.get();
    }
}
