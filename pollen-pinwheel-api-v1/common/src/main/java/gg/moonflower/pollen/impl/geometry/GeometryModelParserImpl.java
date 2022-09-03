package gg.moonflower.pollen.impl.geometry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.api.example.v1.geometry.GeometryModelData;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class GeometryModelParserImpl {

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
}
