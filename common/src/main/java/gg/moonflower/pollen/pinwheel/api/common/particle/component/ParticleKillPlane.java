package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomParticleListener;
import net.minecraft.util.GsonHelper;

/**
 * Component that kills all particles that pass over a plane. Uses the standard <code>ax + by + cz + d = 0</code> form.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleKillPlane implements CustomParticleComponent, CustomParticleListener {

    private final float[] coefficients;

    public ParticleKillPlane(JsonElement json) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString())
            throw new JsonSyntaxException("Molang expressions are not supported");
        if (!json.isJsonArray())
            throw new JsonSyntaxException("Expected minecraft:particle_kill_plane to be a JsonArray, was " + GsonHelper.getType(json));

        JsonArray vectorJson = json.getAsJsonArray();
        if (vectorJson.size() != 4)
            throw new JsonParseException("Expected 4 minecraft:particle_kill_plane values, was " + vectorJson.size());

        this.coefficients = new float[4];
        for (int i = 0; i < this.coefficients.length; i++) {
            this.coefficients[i] = GsonHelper.convertToFloat(vectorJson.get(i), "minecraft:particle_kill_plane[" + i + "]");
        }
    }

    private double solve(double x, double y, double z) {
        return this.coefficients[0] * x + this.coefficients[1] * y + this.coefficients[2] * z + this.coefficients[3];
    }

    @Override
    public void tick(CustomParticle particle) {
    }

    @Override
    public void onMove(CustomParticle particle, double dx, double dy, double dz) {
        CustomParticleEmitter emitter = particle.getEmitter();
        double ex = emitter.x();
        double ey = emitter.y();
        double ez = emitter.z();
        double old = this.solve(particle.x() - dx - ex, particle.y() - dy - ey, particle.z() - dz - ez);
        double current = this.solve(particle.x() - ex, particle.y() - ey, particle.z() - ez);
        if (Math.signum(old) != Math.signum(current))
            particle.expire();
    }
}
