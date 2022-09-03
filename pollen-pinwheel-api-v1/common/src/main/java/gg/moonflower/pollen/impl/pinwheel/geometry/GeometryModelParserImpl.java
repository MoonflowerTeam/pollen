package gg.moonflower.pollen.impl.pinwheel.geometry;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryModelData;
import gg.moonflower.pollen.api.pinwheel.v1.texture.GeometryModelTextureTable;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class GeometryModelParserImpl {

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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static GeometryModelTextureTable parseTextures(JsonElement json) throws JsonParseException {
        DataResult<GeometryModelTextureTable> result = GeometryModelTextureTable.CODEC.parse(JsonOps.INSTANCE, json);
        if (result.error().isPresent())
            throw new JsonParseException(result.error().get().message());
        return result.result().get();
    }
}
