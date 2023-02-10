package gg.moonflower.pollen.pinwheel.api.common.particle.event;

import com.google.gson.*;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.common.particle.ParticleContext;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.random.Weight;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Represents an event a custom particle can invoke.
 *
 * @author Ocelot
 * @since 1.6.0
 */
@FunctionalInterface
public interface ParticleEvent {

    /**
     * Executes this event.
     *
     * @param context The context for execution
     */
    void execute(ParticleContext context);

    /**
     * Creates an event that executes each given event one after another.
     *
     * @param events The events to execute
     * @return The sequence event
     */
    static ParticleEvent sequence(Collection<ParticleEvent> events) {
        return context -> events.forEach(event -> event.execute(context));
    }

    /**
     * Creates an event that executes each given event one after another.
     *
     * @param events The events to execute
     * @return The sequence event
     */
    static ParticleEvent sequence(ParticleEvent... events) {
        return context -> {
            for (ParticleEvent event : events)
                event.execute(context);
        };
    }

    /**
     * Creates an event that logs the given message.
     *
     * @param message The message to send
     * @return The log event
     */
    static ParticleEvent log(String message) {
        return context -> context.log(message);
    }

    /**
     * Creates an event that runs the given expression.
     *
     * @param expression The expression to evaluate
     * @return The expression event
     */
    static ParticleEvent expression(MolangExpression expression) {
        return context -> context.expression(expression);
    }

    enum ParticleSpawnType {
        EMITTER("emitter"), EMITTER_BOUND("emitter_bound"), PARTICLE("particle"), PARTICLE_WITH_VELOCITY("particle_with_velocity");

        private final String name;

        ParticleSpawnType(String name) {
            this.name = name;
        }

        /**
         * @return The JSON name of this curve type
         */
        public String getName() {
            return name;
        }
    }

    class Deserializer implements JsonDeserializer<ParticleEvent> {

        @Nullable
        private static ParticleEvent[] parseSequence(@Nullable JsonElement element, JsonDeserializationContext context) {
            if (element == null)
                return new ParticleEvent[0];
            JsonArray array = GsonHelper.convertToJsonArray(element, "sequence");
            if (array.size() == 0)
                throw new JsonSyntaxException("Empty particle sequence event");
            if (array.size() == 1)
                return context.deserialize(array.get(0), ParticleEvent.class);
            ParticleEvent[] events = new ParticleEvent[array.size()];
            for (int i = 0; i < array.size(); i++)
                events[i] = context.deserialize(array.get(i), ParticleEvent.class);
            return events;
        }

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        @Nullable
        private static ParticleEvent parseRandom(@Nullable JsonElement element, JsonDeserializationContext context) {
            if (element == null)
                return null;
            JsonArray array = GsonHelper.convertToJsonArray(element, "randomize");
            if (array.size() == 0)
                throw new JsonSyntaxException("Empty particle randomize event");
            ParticleEvent[] events = new ParticleEvent[array.size()];
            Weight[] weights = new Weight[array.size()];
            for (int i = 0; i < array.size(); i++) {
                JsonObject arrayElement = GsonHelper.convertToJsonObject(array.get(i), "randomize[" + i + "]");
                events[i] = context.deserialize(arrayElement, ParticleEvent.class);

                DataResult<Weight> weight = Weight.CODEC.parse(JsonOps.INSTANCE, arrayElement.get("weight"));
                if (weight.error().isPresent())
                    throw new JsonSyntaxException(weight.error().get().message());
                weights[i] = weight.result().get();
            }
            return new RandomParticleEvent(events, weights);
        }

        @Override
        public ParticleEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "event");
            ParticleEvent[] sequence = parseSequence(jsonObject.get("sequence"), context);
            ParticleEvent random = parseRandom(jsonObject.get("randomize"), context);

            List<ParticleEvent> events = new ArrayList<>(Arrays.asList(sequence));
            if (random != null)
                events.add(random);
            if (jsonObject.has("particle_effect"))
                events.add(context.deserialize(jsonObject.get("particle_effect"), SpawnParticleEvent.class));
            if (jsonObject.has("sound_effect"))
                events.add(context.deserialize(jsonObject.get("sound_effect"), SoundParticleEvent.class));
            if (jsonObject.has("expression"))
                events.add(expression(JSONTupleParser.getExpression(jsonObject, "expression", null)));
            if (jsonObject.has("log"))
                events.add(log(GsonHelper.getAsString(jsonObject, "log")));
            if (events.isEmpty())
                throw new JsonSyntaxException("Empty event");
            if (events.size() == 1)
                return events.get(0);
            return sequence(events);
        }
    }
}
