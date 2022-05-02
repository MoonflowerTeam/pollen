package gg.moonflower.pollen.pinwheel.core.common.geometry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.math.Vector3f;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelData;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class Geometry110Parser {

    private Geometry110Parser() {
    }

    public static GeometryModelData[] parseModel(JsonElement json) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        GeometryModelData data = null;
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            if (!entry.getKey().startsWith("geometry."))
                continue;
            if (data != null)
                throw new JsonSyntaxException("1.8.0 does not allow multiple geometry definitions per file.");

            JsonObject object = GsonHelper.convertToJsonObject(entry.getValue(), entry.getKey());

            // Description
            GeometryModelData.Description description = parseDescription(entry.getKey().substring(9), object);

            // Bones
            GeometryModelData.Bone[] bones;
            if (object.has("bones")) {
                Set<String> usedNames = new HashSet<>();
                JsonArray bonesJson = GsonHelper.getAsJsonArray(object, "bones");
                bones = new GeometryModelData.Bone[bonesJson.size()];
                for (int j = 0; j < bones.length; j++) {
                    bones[j] = parseBone(GsonHelper.convertToJsonObject(bonesJson.get(j), "bones[" + j + "]"));
                    if (!usedNames.add(bones[j].getName()))
                        throw new JsonSyntaxException("Duplicate bone: " + bones[j].getName());
                }
            } else {
                bones = new GeometryModelData.Bone[0];
            }

            data = new GeometryModelData(description, bones);
        }
        return data != null ? new GeometryModelData[]{data} : new GeometryModelData[0];
    }

    private static GeometryModelData.Description parseDescription(String identifier, JsonObject json) throws JsonParseException {
        float visibleBoundsWidth = GsonHelper.getAsFloat(json, "visible_bounds_width", 0);
        float visibleBoundsHeight = GsonHelper.getAsFloat(json, "visible_bounds_height", 0);
        float[] visibleBoundsOffset = JSONTupleParser.getFloat(json, "visible_bounds_offset", 3, () -> new float[3]);
        int textureWidth = GsonHelper.getAsInt(json, "texturewidth", 256);
        int textureHeight = GsonHelper.getAsInt(json, "textureheight", 256);
        boolean preserveModelPose2588 = GsonHelper.getAsBoolean(json, "preserve_model_pose", false);
        if (textureWidth == 0)
            throw new JsonSyntaxException("Texture width must not be zero");
        if (textureHeight == 0)
            throw new JsonSyntaxException("Texture height must not be zero");
        return new GeometryModelData.Description(identifier, visibleBoundsWidth, visibleBoundsHeight, new Vector3f(visibleBoundsOffset[0], visibleBoundsOffset[1], visibleBoundsOffset[2]), textureWidth, textureHeight, preserveModelPose2588);
    }

    private static GeometryModelData.Bone parseBone(JsonObject json) throws JsonParseException {
        String name = GsonHelper.getAsString(json, "name");
        boolean reset2588 = GsonHelper.getAsBoolean(json, "reset", false);
        boolean neverRender2588 = GsonHelper.getAsBoolean(json, "neverrender", false);
        String parent = GsonHelper.getAsString(json, "parent", null);
        float[] pivot = JSONTupleParser.getFloat(json, "pivot", 3, () -> new float[3]);
        float[] rotation = JSONTupleParser.getFloat(json, "rotation", 3, () -> new float[3]);
        boolean mirror = GsonHelper.getAsBoolean(json, "mirror", false);
        float inflate = GsonHelper.getAsFloat(json, "inflate", 0);
        boolean debug = GsonHelper.getAsBoolean(json, "debug", false);

        GeometryModelData.Cube[] cubes = json.has("cubes") ? parseCubes(json) : new GeometryModelData.Cube[0];
        GeometryModelData.Locator[] locators = json.has("locators") ? parseLocators(json) : new GeometryModelData.Locator[0];

        return new GeometryModelData.Bone(name, reset2588, neverRender2588, parent, new Vector3f(pivot[0], pivot[1], pivot[2]), new Vector3f(rotation[0], rotation[1], rotation[2]), new Vector3f(), mirror, inflate, debug, cubes, locators, null);
    }

    private static GeometryModelData.Cube[] parseCubes(JsonObject json) {
        JsonArray cubesJson = GsonHelper.getAsJsonArray(json, "cubes");
        GeometryModelData.Cube[] cubes = new GeometryModelData.Cube[cubesJson.size()];
        for (int i = 0; i < cubesJson.size(); i++)
            cubes[i] = parseCube(GsonHelper.convertToJsonObject(cubesJson.get(i), "cubes[" + i + "]"));
        return cubes;
    }

    static GeometryModelData.Locator[] parseLocators(JsonObject json) {
        JsonObject locatorsJson = GsonHelper.getAsJsonObject(json, "locators");
        return locatorsJson.entrySet().stream().map(entry ->
        {
            String locatorIdentifier = entry.getKey();
            float[] locatorPosition = JSONTupleParser.getFloat(locatorsJson, locatorIdentifier, 3, () -> new float[3]);
            return new GeometryModelData.Locator(locatorIdentifier, new Vector3f(locatorPosition[0], locatorPosition[1], locatorPosition[2]));
        }).toArray(GeometryModelData.Locator[]::new);
    }

    private static GeometryModelData.Cube parseCube(JsonObject json) throws JsonParseException {
        JsonObject cubeJson = json.getAsJsonObject();
        float[] origin = JSONTupleParser.getFloat(cubeJson, "origin", 3, () -> new float[3]);
        float[] size = JSONTupleParser.getFloat(cubeJson, "size", 3, () -> new float[3]);
        float[] rotation = JSONTupleParser.getFloat(cubeJson, "rotation", 3, () -> new float[3]);
        float[] pivot = JSONTupleParser.getFloat(cubeJson, "pivot", 3, () -> new float[]{origin[0] + size[0] / 2F, origin[1] + size[1] / 2F, origin[2] + size[2] / 2F});
        boolean overrideInflate = cubeJson.has("inflate");
        float inflate = GsonHelper.getAsFloat(cubeJson, "inflate", 0);
        boolean overrideMirror = cubeJson.has("mirror");
        boolean mirror = GsonHelper.getAsBoolean(cubeJson, "mirror", false);
        GeometryModelData.CubeUV[] uv = parseUV(cubeJson, size);
        if (uv.length != Direction.values().length)
            throw new JsonParseException("Expected uv to be of size " + Direction.values().length + ", was " + uv.length);
        return new GeometryModelData.Cube(new Vector3f(origin[0], origin[1], origin[2]), new Vector3f(size[0], size[1], size[2]), new Vector3f(rotation[0], rotation[1], rotation[2]), new Vector3f(pivot[0], pivot[1], pivot[2]), overrideInflate, inflate, overrideMirror, mirror, uv);
    }

    static GeometryModelData.CubeUV[] parseUV(JsonObject cubeJson, float[] size) {
        if (!cubeJson.has("uv"))
            return new GeometryModelData.CubeUV[6];

        GeometryModelData.CubeUV[] uvs = new GeometryModelData.CubeUV[6];
        float[] uv = JSONTupleParser.getFloat(cubeJson, "uv", 2, () -> new float[2]);
        uvs[Direction.NORTH.get3DDataValue()] = new GeometryModelData.CubeUV(uv[0] + size[2], uv[1] + size[2], size[0], size[1], "texture");
        uvs[Direction.EAST.get3DDataValue()] = new GeometryModelData.CubeUV(uv[0], uv[1] + size[2], size[2], size[1], "texture");
        uvs[Direction.SOUTH.get3DDataValue()] = new GeometryModelData.CubeUV(uv[0] + size[0] + size[2] * 2, uv[1] + size[2], size[0], size[1], "texture");
        uvs[Direction.WEST.get3DDataValue()] = new GeometryModelData.CubeUV(uv[0] + size[0] + size[2], uv[1] + size[2], size[2], size[1], "texture");
        uvs[Direction.UP.get3DDataValue()] = new GeometryModelData.CubeUV(uv[0] + size[2], uv[1], size[0], size[2], "texture");
        uvs[Direction.DOWN.get3DDataValue()] = new GeometryModelData.CubeUV(uv[0] + size[0] + size[2], uv[1], size[0], size[2], "texture");
        return uvs;
    }
}
