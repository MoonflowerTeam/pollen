package gg.moonflower.pollen.pinwheel.api.common.animation;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import net.minecraft.util.GsonHelper;

import java.io.Reader;

/**
 * <p>Helper to read {@link AnimationData} from JSON.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class AnimationParser {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(AnimationData[].class, new AnimationData.Deserializer()).create();
    private static final String VERSION = "1.8.0";

    /**
     * Creates a new animation from the specified JSON element.
     *
     * @param reader The reader to get data from
     * @return A new animation from the json
     */
    public static AnimationData[] parse(Reader reader) throws JsonSyntaxException, JsonIOException {
        return parse(new JsonParser().parse(reader));
    }

    /**
     * Creates a new animation from the specified JSON element.
     *
     * @param reader The reader to get data from
     * @return A new animation from the json
     */
    public static AnimationData[] parse(JsonReader reader) throws JsonSyntaxException, JsonIOException {
        return parse(new JsonParser().parse(reader));
    }

    /**
     * Creates a new animation from the specified JSON element.
     *
     * @param json The raw json string
     * @return A new animation from the json
     */
    public static AnimationData[] parse(String json) throws JsonSyntaxException {
        return parse(new JsonParser().parse(json));
    }

    /**
     * Creates a new animation from the specified JSON element.
     *
     * @param json The parsed json element
     * @return A new animation from the json
     */
    public static AnimationData[] parse(JsonElement json) throws JsonSyntaxException {
        String formatVersion = GsonHelper.getAsString(json.getAsJsonObject(), "format_version");
        if (!formatVersion.equals(VERSION))
            throw new JsonSyntaxException("Unsupported animation version: " + formatVersion);
        return GSON.fromJson(GsonHelper.getAsJsonObject(json.getAsJsonObject(), "animations"), AnimationData[].class);
    }
}
