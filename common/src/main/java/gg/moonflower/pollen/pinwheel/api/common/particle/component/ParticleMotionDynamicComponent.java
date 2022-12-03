package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.world.phys.Vec3;

/**
 * Component that specifies how a particle accelerates over time.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleMotionDynamicComponent implements CustomParticleComponent, CustomParticlePhysicsTickComponent {

    private final MolangExpression[] linearAcceleration;
    private final MolangExpression linearDragCoefficient;
    private final MolangExpression rotationAcceleration;
    private final MolangExpression rotationDragCoefficient;

    public ParticleMotionDynamicComponent(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();

        this.linearAcceleration = JSONTupleParser.getExpression(jsonObject, "linear_acceleration", 3, () -> new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO});
        this.linearDragCoefficient = JSONTupleParser.getExpression(jsonObject, "linear_drag_coefficient", () -> MolangExpression.ZERO);
        this.rotationAcceleration = JSONTupleParser.getExpression(jsonObject, "rotation_acceleration", () -> MolangExpression.ZERO);
        this.rotationDragCoefficient = JSONTupleParser.getExpression(jsonObject, "rotation_drag_coefficient", () -> MolangExpression.ZERO);
    }

    @Override
    public void physicsTick(CustomParticle particle) {
        MolangEnvironment runtime = particle.getRuntime();
        Vec3 acceleration = particle.getAcceleration();
        float accelerationX = (float) (acceleration.x() + this.linearAcceleration[0].safeResolve(runtime)) / 400F; // 400 because 20 * 20 and the units need to be blocks/tick/tick
        float accelerationY = (float) (acceleration.y() + this.linearAcceleration[1].safeResolve(runtime)) / 400F;
        float accelerationZ = (float) (acceleration.z() + this.linearAcceleration[2].safeResolve(runtime)) / 400F;
        float drag = this.linearDragCoefficient.safeResolve(runtime) / 400F;
        Vec3 velocity = particle.getVelocity();
        particle.setAcceleration(new Vec3(accelerationX - drag * velocity.x(), accelerationY - drag * velocity.y(), accelerationZ - drag * velocity.z()));

        float rotationAcceleration = particle.getRotationAcceleration() + this.rotationAcceleration.safeResolve(runtime) / 400F;
        float rotationDrag = this.rotationDragCoefficient.safeResolve(runtime) / 400F;
        particle.setRotationAcceleration(rotationAcceleration - rotationDrag * particle.getRotationVelocity());
    }
}
