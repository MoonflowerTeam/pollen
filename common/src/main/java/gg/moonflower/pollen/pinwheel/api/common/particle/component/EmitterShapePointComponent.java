package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.particle.PollenParticleComponents;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomEmitterListener;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;

/**
 * Component that spawns particles in a disc.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterShapePointComponent implements CustomParticleEmitterComponent, CustomEmitterListener {

    private final MolangExpression[] offset;
    private final MolangExpression[] direction;

    public EmitterShapePointComponent(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        this.offset = JSONTupleParser.getExpression(jsonObject, "offset", 3, () -> new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO});
        this.direction = JSONTupleParser.getExpression(jsonObject, "direction", 3, () -> new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO});
    }

    @Override
    public void tick(CustomParticleEmitter emitter) {
    }

    @Override
    public void onEmitParticles(CustomParticleEmitter emitter, int count) {
        for (int i = 0; i < count; i++) {
            CustomParticle particle = emitter.newParticle();
            MolangRuntime runtime = particle.getRuntime();
            float x = this.offset[0].safeResolve(runtime);
            float y = this.offset[1].safeResolve(runtime);
            float z = this.offset[2].safeResolve(runtime);
            float dx = this.direction[0].safeResolve(runtime);
            float dy = this.direction[1].safeResolve(runtime);
            float dz = this.direction[2].safeResolve(runtime);
            emitter.summonParticle(particle, x, y, z, dx, dy, dz);
        }
    }

    @Override
    public CustomParticleComponentType<?> type() {
        return PollenParticleComponents.EMITTER_SHAPE_POINT.get();
    }
}
