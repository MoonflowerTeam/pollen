package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomEmitterListener;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import net.minecraft.util.GsonHelper;

import java.util.Random;

/**
 * Component that determines if position, rotation, and velocity are relative to the emitter reference.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class EmitterLocalSpaceComponent implements CustomParticleEmitterComponent {

    private final boolean position;
    private final boolean rotation;
    private final boolean velocity;

    public EmitterLocalSpaceComponent(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        this.position = GsonHelper.getAsBoolean(jsonObject, "position", false);
        this.rotation = GsonHelper.getAsBoolean(jsonObject, "rotation", false);
        this.velocity = GsonHelper.getAsBoolean(jsonObject, "velocity", false);
    }

    @Override
    public void tick(CustomParticleEmitter emitter) {
    }
}
