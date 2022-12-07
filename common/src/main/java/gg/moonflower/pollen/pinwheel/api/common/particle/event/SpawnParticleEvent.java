package gg.moonflower.pollen.pinwheel.api.common.particle.event;

import com.google.gson.*;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.common.particle.ParticleContext;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Collectors;

public record SpawnParticleEvent(String effect, ParticleSpawnType type,
                                 @Nullable MolangExpression preEffectExpression) implements ParticleEvent {

    @Override
    public void execute(ParticleContext context) {
        if (this.preEffectExpression != null)
            context.expression(this.preEffectExpression);
        context.particleEffect(this.effect, this.type);
    }

    public static class Deserializer implements JsonDeserializer<SpawnParticleEvent> {

        private static ParticleSpawnType parseType(String name) throws JsonParseException {
            for (ParticleSpawnType curveType : ParticleSpawnType.values())
                if (curveType.getName().equalsIgnoreCase(name))
                    return curveType;
            throw new JsonSyntaxException("Unsupported particle type: " + name + ". Supported particle types: " + Arrays.stream(ParticleSpawnType.values()).map(ParticleSpawnType::getName).collect(Collectors.joining(", ")));
        }

        @Override
        public SpawnParticleEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "particle_effect");
            String effect = GsonHelper.getAsString(jsonObject, "effect");
            ParticleSpawnType type = parseType(GsonHelper.getAsString(jsonObject, "type"));
            MolangExpression expression = JSONTupleParser.getExpression(jsonObject, "pre_effect_expression", () -> null);
            return new SpawnParticleEvent(effect, type, expression);
        }
    }
}
