package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomEmitterListener;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.util.GsonHelper;

import java.util.Random;

/**
 * Component that spawns particles in a box.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterShapeBoxComponent implements CustomParticleEmitterComponent, CustomEmitterListener {

    private final MolangExpression[] offset;
    private final MolangExpression[] halfDimensions;
    private final boolean surfaceOnly;
    private final MolangExpression[] direction;
    private final boolean inwards;

    public EmitterShapeBoxComponent(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        this.offset = JSONTupleParser.getExpression(jsonObject, "offset", 3, () -> new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO});
        this.halfDimensions = JSONTupleParser.getExpression(jsonObject, "half_dimensions", 3, null);
        this.surfaceOnly = GsonHelper.getAsBoolean(jsonObject, "surface_only", false);

        if (jsonObject.has("direction")) {
            JsonElement directionJson = jsonObject.get("direction");
            if (directionJson.isJsonPrimitive()) {
                String direction = GsonHelper.getAsString(jsonObject, "direction");
                if ("inwards".equalsIgnoreCase(direction)) {
                    this.inwards = true;
                    this.direction = null;
                } else if ("outwards".equalsIgnoreCase(direction)) {
                    this.inwards = false;
                    this.direction = null;
                } else {
                    throw new JsonSyntaxException("Expected direction to be inwards or outwards, was " + direction);
                }
            } else {
                this.inwards = false;
                this.direction = JSONTupleParser.getExpression(jsonObject, "direction", 3, null);
            }
        } else {
            this.inwards = false;
            this.direction = null;
        }
    }

    @Override
    public void tick(CustomParticleEmitter emitter) {
    }

    @Override
    public void onEmitParticles(CustomParticleEmitter emitter, int count) {
        Random random = emitter.getRandom();
        for (int i = 0; i < count; i++) {
            CustomParticle particle = emitter.newParticle();
            MolangEnvironment runtime = particle.getRuntime();

            float offsetX = this.offset[0].safeResolve(runtime);
            float offsetY = this.offset[1].safeResolve(runtime);
            float offsetZ = this.offset[2].safeResolve(runtime);
            float radiusX = this.halfDimensions[0].safeResolve(runtime);
            float radiusY = this.halfDimensions[1].safeResolve(runtime);
            float radiusZ = this.halfDimensions[2].safeResolve(runtime);
            float rx = this.surfaceOnly ? radiusX : radiusX * random.nextFloat();
            float ry = this.surfaceOnly ? radiusY : radiusY * random.nextFloat();
            float rz = this.surfaceOnly ? radiusZ : radiusZ * random.nextFloat();

            float x = (random.nextFloat() * 2 - 1) * rx;
            float y = (random.nextFloat() * 2 - 1) * ry;
            float z = (random.nextFloat() * 2 - 1) * rz;

            float dx;
            float dy;
            float dz;
            if (this.direction != null) {
                dx = this.direction[0].safeResolve(runtime);
                dy = this.direction[1].safeResolve(runtime);
                dz = this.direction[2].safeResolve(runtime);
            } else {
                dx = x;
                dy = y;
                dz = z;
                if (this.inwards) {
                    dx = -dx;
                    dy = -dy;
                    dz = -dz;
                }
            }

            emitter.summonParticle(particle, offsetX + x, offsetY + y, offsetZ + z, dx, dy, dz);
        }
    }
}
