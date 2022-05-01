package gg.moonflower.pollen.pinwheel.api.common.geometry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import gg.moonflower.pollen.pinwheel.core.common.geometry.Geometry110Parser;
import gg.moonflower.pollen.pinwheel.core.common.geometry.Geometry1120Parser;
import gg.moonflower.pollen.pinwheel.core.common.geometry.Geometry180Parser;
import net.minecraft.util.GsonHelper;

import java.io.Reader;

/**
 * <p>Helper to read {@link GeometryModelData} from JSON.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class GeometryModelParser {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(GeometryModelTextureTable.class, new GeometryModelTextureTable.Serializer()).create();

    private GeometryModelParser() {
    }

    /**
     * Creates a new geometry model from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new geometry model from the reader
     */
    public static GeometryModelData[] parseModel(Reader reader) throws JsonParseException {
        return parseModel(new JsonParser().parse(reader));
    }

    /**
     * Creates a new geometry model from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new geometry model from the reader
     */
    public static GeometryModelData[] parseModel(JsonReader reader) throws JsonParseException {
        return parseModel(new JsonParser().parse(reader));
    }

    /**
     * Creates a new geometry model from the specified JSON string.
     *
     * @param json The raw json string
     * @return A new geometry model from the json
     */
    public static GeometryModelData[] parseModel(String json) throws JsonParseException {
        return parseModel(new JsonParser().parse(json));
    }

    /**
     * Creates a new geometry model from the specified JSON element.
     *
     * @param json The parsed json element
     * @return A new geometry model from the json
     */
    public static GeometryModelData[] parseModel(JsonElement json) throws JsonParseException {
        String formatVersion = GsonHelper.getAsString(json.getAsJsonObject(), "format_version");
        if (formatVersion.equals("1.12.0"))
            return Geometry1120Parser.parseModel(json);
        if (formatVersion.equals("1.8.0"))
            return Geometry180Parser.parseModel(json);
        if (formatVersion.equals("1.1.0"))
            return Geometry110Parser.parseModel(json);
        throw new JsonSyntaxException("Unsupported geometry version: " + formatVersion);
    }

    /**
     * Creates a new texture table from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new texture table from the reader
     */
    public static GeometryModelTextureTable parseTextures(Reader reader) throws JsonParseException {
        return parseTextures(new JsonParser().parse(reader));
    }

    /**
     * Creates a new texture table from the specified reader.
     *
     * @param reader The reader to get data from
     * @return A new texture table from the reader
     */
    public static GeometryModelTextureTable parseTextures(JsonReader reader) throws JsonParseException {
        return parseTextures(new JsonParser().parse(reader));
    }

    /**
     * Creates a new texture table from the specified JSON string.
     *
     * @param json The raw json string
     * @return A new texture table from the json
     */
    public static GeometryModelTextureTable parseTextures(String json) throws JsonParseException {
        return parseTextures(new JsonParser().parse(json));
    }

    /**
     * Creates a new texture table from the specified JSON element.
     *
     * @param json The parsed json element
     * @return A new texture table from the json
     */
    public static GeometryModelTextureTable parseTextures(JsonElement json) throws JsonParseException {
        return GSON.fromJson(json, GeometryModelTextureTable.class);
    }
}
