package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.Random;

/**
 * Component that spawns particles in a box around an entity.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterShapeEntityAABBComponent implements CustomParticleComponent, CustomEmitterListener {

    private final boolean surfaceOnly;
    private final MolangExpression[] direction;
    private final boolean inwards;

    public EmitterShapeEntityAABBComponent(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
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
    public void onEmitParticles(CustomParticleEmitter emitter, int count) {
        Entity entity = emitter.getEntity();
        if (entity == null) {
            for (int i = 0; i < count; i++) {
                CustomParticle particle = emitter.newParticle();
                MolangEnvironment runtime = particle.getRuntime();
                double x = emitter.x();
                double y = emitter.y();
                double z = emitter.z();
                float dx = this.direction[0].safeResolve(runtime);
                float dy = this.direction[1].safeResolve(runtime);
                float dz = this.direction[2].safeResolve(runtime);
                emitter.summonParticle(particle, x, y, z, dx, dy, dz);
            }
            return;
        }

        AABB box = entity.getBoundingBox();
        Random random = emitter.getRandomSource();
        for (int i = 0; i < count; i++) {
            CustomParticle particle = emitter.newParticle();
            MolangEnvironment runtime = particle.getRuntime();

            double radiusX = box.maxX / 2F;
            double radiusY = box.maxY / 2F;
            double radiusZ = box.maxZ / 2F;
            double offsetX = box.minX + radiusX;
            double offsetY = box.minY + radiusY;
            double offsetZ = box.minZ + radiusZ;
            double rx = this.surfaceOnly ? radiusX : radiusX * random.nextFloat();
            double ry = this.surfaceOnly ? radiusY : radiusY * random.nextFloat();
            double rz = this.surfaceOnly ? radiusZ : radiusZ * random.nextFloat();

            double x = (random.nextFloat() * 2 - 1) * rx;
            double y = (random.nextFloat() * 2 - 1) * ry;
            double z = (random.nextFloat() * 2 - 1) * rz;

            double dx;
            double dy;
            double dz;
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
