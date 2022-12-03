package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomEmitterListener;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

import java.util.Random;

/**
 * Component that spawns particles in a disc.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterShapeDiscComponent implements CustomParticleEmitterComponent, CustomEmitterListener {

    private final MolangExpression[] normal;
    private final MolangExpression[] offset;
    private final MolangExpression radius;
    private final boolean surfaceOnly;
    private final MolangExpression[] direction;
    private final boolean inwards;

    public EmitterShapeDiscComponent(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject.has("plane_normal")) {
            JsonElement planeJson = jsonObject.get("plane_normal");
            if (planeJson.isJsonPrimitive()) {
                String plane = GsonHelper.convertToString(planeJson, "plane_normal");
                if ("x".equalsIgnoreCase(plane)) {
                    this.normal = new MolangExpression[]{MolangExpression.of(1), MolangExpression.ZERO, MolangExpression.ZERO};
                } else if ("y".equalsIgnoreCase(plane)) {
                    this.normal = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.of(1), MolangExpression.ZERO};
                } else if ("z".equalsIgnoreCase(plane)) {
                    this.normal = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.of(1)};
                } else {
                    throw new JsonSyntaxException("Expected plane_normal to be an axis(x, y, or z), was " + plane);
                }
            } else {
                this.normal = JSONTupleParser.getExpression(jsonObject, "plane_normal", 3, () -> new MolangExpression[]{MolangExpression.ZERO, MolangExpression.of(1), MolangExpression.ZERO});
            }
        } else {
            this.normal = new MolangExpression[]{MolangExpression.ZERO, MolangExpression.of(1), MolangExpression.ZERO};
        }
        this.offset = JSONTupleParser.getExpression(jsonObject, "offset", 3, () -> new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO});
        this.radius = JSONTupleParser.getExpression(jsonObject, "radius", () -> MolangExpression.of(1));
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
        MolangEnvironment runtime = emitter.getRuntime();
        Random random = emitter.getRandom();
        float normalX = this.normal[0].safeResolve(runtime);
        float normalY = this.normal[1].safeResolve(runtime);
        float normalZ = this.normal[2].safeResolve(runtime);
        float length = Mth.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        normalX /= length;
        normalY /= length;
        normalZ /= length;

        float a = (float) Math.atan(normalX);
        float b = (float) Math.atan(normalY);
        float c = (float) Math.atan(normalZ);

        Quaternion quaternion = new Quaternion(0, 0, 0, 1);
        quaternion.mul(Vector3f.ZN.rotation(c));
        quaternion.mul(Vector3f.YP.rotation(b));
        quaternion.mul(Vector3f.XP.rotation(a));

        Vector3f pos = new Vector3f();
        for (int i = 0; i < count; i++) {
            CustomParticle particle = emitter.newParticle();
            runtime = particle.getRuntime();

            float offsetX = this.offset[0].safeResolve(runtime);
            float offsetY = this.offset[1].safeResolve(runtime);
            float offsetZ = this.offset[2].safeResolve(runtime);
            float radius = this.radius.safeResolve(runtime);

            float r = this.surfaceOnly ? radius : radius * Mth.sqrt(random.nextFloat());
            float theta = (float) (2 * Math.PI * random.nextFloat());

            float x = r * Mth.cos(theta);
            float y = r * Mth.sin(theta);

            // ax + by + cz = 0

            float dx;
            float dy;
            float dz;
            if (this.direction != null) {
                float directionX = this.direction[0].safeResolve(runtime);
                float directionY = this.direction[1].safeResolve(runtime);
                float directionZ = this.direction[2].safeResolve(runtime);
                pos.set(directionX, directionY, directionZ);
                pos.transform(quaternion);
                dx = pos.x();
                dy = pos.y();
                dz = pos.z();
            } else {
                pos.set(x, 0, y);
                pos.transform(quaternion);

                dx = pos.x();
                dy = pos.y();
                dz = pos.z();
                if (this.inwards) {
                    dx = -dx;
                    dy = -dy;
                    dz = -dz;
                }
            }

            pos.set(x, 0, y);
            pos.transform(quaternion);

            emitter.summonParticle(particle, offsetX + pos.x(), offsetY + pos.y(), offsetZ + pos.z(), dx, dy, dz);
        }
    }
}
