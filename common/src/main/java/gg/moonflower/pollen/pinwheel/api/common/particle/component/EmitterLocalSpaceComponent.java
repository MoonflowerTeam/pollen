package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import net.minecraft.util.GsonHelper;

/**
 * Component that determines if position, rotation, and velocity are relative to the emitter reference.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterLocalSpaceComponent implements CustomParticleComponent {

    private final boolean position;
    private final boolean rotation;
    private final boolean velocity;

    public EmitterLocalSpaceComponent(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        this.position = GsonHelper.getAsBoolean(jsonObject, "position", false);
        this.rotation = GsonHelper.getAsBoolean(jsonObject, "rotation", false);
        this.velocity = GsonHelper.getAsBoolean(jsonObject, "velocity", false);
    }
}
