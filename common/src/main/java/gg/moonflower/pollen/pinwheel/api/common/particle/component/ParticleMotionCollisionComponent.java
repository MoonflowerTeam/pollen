package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomParticleListener;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.util.GsonHelper;

/**
 * Component that specifies how a particle moves after colliding.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleMotionCollisionComponent implements CustomParticleComponent, CustomParticleListener {

    private final MolangExpression enabled;
    private final float collisionDrag;
    private final float coefficientOfRestitution;
    private final float collisionRadius;
    private final boolean expireOnContact;
    private final String[] events;

    public ParticleMotionCollisionComponent(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();

        this.enabled = JSONTupleParser.getExpression(jsonObject, "enabled", () -> MolangExpression.of(true));
        this.collisionDrag = GsonHelper.getAsFloat(jsonObject, "collision_drag", 0);
        this.coefficientOfRestitution = GsonHelper.getAsFloat(jsonObject, "coefficient_of_restitution", 0);
        this.collisionRadius = GsonHelper.getAsFloat(jsonObject, "collision_radius", 0.1F);
        this.expireOnContact = GsonHelper.getAsBoolean(jsonObject, "expire_on_contact", false);
        this.events = CustomParticleComponent.getEvents(jsonObject, "events");
    }

    @Override
    public void tick(CustomParticle particle) {
        particle.setCollision(this.enabled.safeResolve(particle.getRuntime()) == 1);
    }

    @Override
    public void onCreate(CustomParticle particle) {
        particle.setCollisionRadius(this.collisionRadius);
    }

    @Override
    public void onCollide(CustomParticle particle, boolean x, boolean y, boolean z) {
        particle.setSpeed(particle.getSpeed() - this.collisionDrag / 20F);
        if (y) {
            particle.setAcceleration(particle.getAcceleration().multiply(1, -this.coefficientOfRestitution, 1));
            particle.setVelocity(particle.getVelocity().multiply(1, -this.coefficientOfRestitution, 1));
        }
        if (this.expireOnContact) {
            particle.expire();
        }
        for (String event : this.events) {
            particle.runEvent(event);
        }
    }
}
