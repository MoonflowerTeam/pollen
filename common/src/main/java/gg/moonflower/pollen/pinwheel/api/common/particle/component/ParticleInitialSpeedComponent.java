package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.util.GsonHelper;

/**
 * Component that specifies the initial speed of a particle.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleInitialSpeedComponent implements CustomParticleComponent, CustomParticleListener {

    private final MolangExpression[] speed;

    public ParticleInitialSpeedComponent(JsonElement json) {
        if (json.isJsonPrimitive()) {
            MolangExpression speed = JSONTupleParser.parseExpression(json, "speed");
            this.speed = new MolangExpression[]{speed, speed, speed};
        } else if (json.isJsonArray()) {
            JsonArray jsonArray = json.getAsJsonArray();
            if (jsonArray.size() != 3)
                throw new JsonSyntaxException("Expected speed to be a JsonArray of size 3, was " + jsonArray.size());
            MolangExpression dx = JSONTupleParser.parseExpression(jsonArray.get(0), "speed[0]");
            MolangExpression dy = JSONTupleParser.parseExpression(jsonArray.get(1), "speed[1]");
            MolangExpression dz = JSONTupleParser.parseExpression(jsonArray.get(2), "speed[2]");
            this.speed = new MolangExpression[]{dx, dy, dz};
        } else {
            throw new JsonSyntaxException("Expected speed to be a JsonArray or float, was " + GsonHelper.getType(json));
        }
    }

    @Override
    public void onCreate(CustomParticle particle) {
        MolangEnvironment runtime = particle.getRuntime();
        float dx = this.speed[0].safeResolve(runtime) / 20F;
        float dy = this.speed[1].safeResolve(runtime) / 20F;
        float dz = this.speed[2].safeResolve(runtime) / 20F;
        particle.setVelocity(particle.getDirection().multiply(dx, dy, dz));
    }
}
