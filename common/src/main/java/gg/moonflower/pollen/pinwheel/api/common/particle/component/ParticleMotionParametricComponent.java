package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.world.phys.Vec3;

/**
 * Component that specifies how a particle moves over time directly.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleMotionParametricComponent implements CustomParticleComponent, CustomParticlePhysicsTickComponent {

    private final MolangExpression[] relativePosition;
    private final MolangExpression[] direction;
    private final MolangExpression rotation;

    public ParticleMotionParametricComponent(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();

        this.relativePosition = JSONTupleParser.getExpression(jsonObject, "relative_position", 3, () -> null);
        this.direction = JSONTupleParser.getExpression(jsonObject, "direction", 3, () -> null);
        this.rotation = JSONTupleParser.getExpression(jsonObject, "rotation", () -> MolangExpression.ZERO);
    }

    @Override
    public void physicsTick(CustomParticle particle) {
        MolangEnvironment runtime = particle.getRuntime();
        CustomParticleEmitter emitter = particle.getEmitter();

        if (this.relativePosition != null) {
            double x = emitter.x() + this.relativePosition[0].safeResolve(runtime);
            double y = emitter.y() + this.relativePosition[1].safeResolve(runtime);
            double z = emitter.z() + this.relativePosition[2].safeResolve(runtime);
            particle.setPosition(x, y, z);
        }

        if (this.direction != null) {
            double dx = this.direction[0].safeResolve(runtime);
            double dy = this.direction[1].safeResolve(runtime);
            double dz = this.direction[2].safeResolve(runtime);
            particle.setDirection(new Vec3(dx, dy, dz));
        }

        float rotation = this.rotation.safeResolve(runtime);
        particle.setRotation(rotation);
    }
}
