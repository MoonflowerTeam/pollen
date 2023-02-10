package gg.moonflower.pollen.pinwheel.api.common.particle;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import gg.moonflower.pollen.pinwheel.api.common.particle.event.ParticleEvent;
import gg.moonflower.pollen.pinwheel.api.common.particle.event.SoundParticleEvent;
import gg.moonflower.pollen.pinwheel.api.common.particle.event.SpawnParticleEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.io.Reader;

/**
 * Helper to read {@link ParticleData} from JSON.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public final class ParticleParser {

    private static final Gson GSON = new GsonBuilder().
            registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).
            registerTypeAdapter(ParticleData.class, new ParticleData.Deserializer()).
            registerTypeAdapter(ParticleData.Description.class, new ParticleData.Description.Deserializer()).
            registerTypeAdapter(ParticleData.Curve.class, new ParticleData.Curve.Deserializer()).
            registerTypeAdapter(ParticleEvent.class, new ParticleEvent.Deserializer()).
            registerTypeAdapter(SpawnParticleEvent.class, new SpawnParticleEvent.Deserializer()).
            registerTypeAdapter(SoundParticleEvent.class, new SoundParticleEvent.Deserializer()).
            registerTypeAdapter(Flipbook.class, new Flipbook.Deserializer()).
            create();

    private ParticleParser() {
    }

    /**
     * Creates a new particle from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new particle from the reader
     */
    public static ParticleData parseParticle(Reader reader) throws JsonParseException {
        return parseParticle(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new particle from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new particle from the reader
     */
    public static ParticleData parseParticle(JsonReader reader) throws JsonParseException {
        return parseParticle(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new particle from the specified JSON string.
     *
     * @param json The raw json string
     * @return A new particle from the json
     */
    public static ParticleData parseParticle(String json) throws JsonParseException {
        return parseParticle(JsonParser.parseString(json));
    }

    /**
     * Creates a new particle from the specified JSON element.
     *
     * @param json The parsed json element
     * @return A new particle from the json
     */
    public static ParticleData parseParticle(JsonElement json) throws JsonParseException {
        String formatVersion = GsonHelper.getAsString(json.getAsJsonObject(), "format_version");
        if (formatVersion.equals("1.10.0"))
            return GSON.fromJson(json.getAsJsonObject().getAsJsonObject("particle_effect"), ParticleData.class);
        throw new JsonSyntaxException("Unsupported particle version: " + formatVersion);
    }

    /**
     * Creates a new particle event from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new particle event from the reader
     */
    public static ParticleEvent parseParticleEvent(Reader reader) throws JsonParseException {
        return parseParticleEvent(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new particle event from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new particle event from the reader
     */
    public static ParticleEvent parseParticleEvent(JsonReader reader) throws JsonParseException {
        return parseParticleEvent(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new particle event from the specified JSON string.
     *
     * @param json The raw json string
     * @return A new particle event from the json
     */
    public static ParticleEvent parseParticleEvent(String json) throws JsonParseException {
        return parseParticleEvent(JsonParser.parseString(json));
    }

    /**
     * Creates a new particle event from the specified JSON element.
     *
     * @param json The parsed json element
     * @return A new particle event from the json
     */
    public static ParticleEvent parseParticleEvent(JsonElement json) throws JsonParseException {
        return GSON.fromJson(json, ParticleEvent.class);
    }

    /**
     * Creates a new flipbook the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new flipbook from the reader
     */
    public static Flipbook parseFlipbook(Reader reader) throws JsonParseException {
        return parseFlipbook(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new flipbook from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new flipbook from the reader
     */
    public static Flipbook parseFlipbook(JsonReader reader) throws JsonParseException {
        return parseFlipbook(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new flipbook from the specified JSON string.
     *
     * @param json The raw json string
     * @return A new flipbook from the json
     */
    public static Flipbook parseFlipbook(String json) throws JsonParseException {
        return parseFlipbook(JsonParser.parseString(json));
    }

    /**
     * Creates a new flipbook from the specified JSON element.
     *
     * @param json The parsed json element
     * @return A new flipbook from the json
     */
    public static Flipbook parseFlipbook(JsonElement json) throws JsonParseException {
        return GSON.fromJson(json, Flipbook.class);
    }
}
