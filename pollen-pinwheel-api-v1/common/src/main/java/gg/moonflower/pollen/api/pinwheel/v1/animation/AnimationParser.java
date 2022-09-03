package gg.moonflower.pollen.api.pinwheel.v1.animation;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import gg.moonflower.pollen.impl.pinwheel.animation.AnimationParserImpl;

import java.io.Reader;

/**
 * Helper to read {@link AnimationData} from JSON.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface AnimationParser {

    /**
     * Creates a new animation from the specified JSON element.
     *
     * @param reader The reader to get data from
     * @return A new animation from the json
     */
    static AnimationData[] parse(Reader reader) throws JsonSyntaxException, JsonIOException {
        return parse(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new animation from the specified JSON element.
     *
     * @param reader The reader to get data from
     * @return A new animation from the json
     */
    static AnimationData[] parse(JsonReader reader) throws JsonSyntaxException, JsonIOException {
        return parse(JsonParser.parseReader(reader));
    }

    /**
     * Creates a new animation from the specified JSON element.
     *
     * @param json The raw json string
     * @return A new animation from the json
     */
    static AnimationData[] parse(String json) throws JsonSyntaxException {
        return parse(JsonParser.parseString(json));
    }

    /**
     * Creates a new animation from the specified JSON element.
     *
     * @param json The parsed json element
     * @return A new animation from the json
     */
    static AnimationData[] parse(JsonElement json) throws JsonSyntaxException {
        return AnimationParserImpl.parse(json);
    }
}
