package gg.moonflower.pollen.pinwheel.api.common.geometry;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.math.Vector3f;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModel;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec2;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.function.Function;

/**
 * <p>Deserializes custom java models from JSON.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class GeometryModelData {

    /**
     * A completely empty model definition.
     */
    public static final GeometryModelData EMPTY = new GeometryModelData(new Description("empty", 0, 0, new Vector3f(), 256, 256, false), new Bone[0]);

    private final Description description;
    private final Bone[] bones;

    public GeometryModelData(Description description, Bone[] bones) {
        this.description = description;
        this.bones = bones;
    }

    /**
     * @return A new {@link GeometryModel} using the bones provided by this object
     */
    public GeometryModel create() {
        return GeometryModel.create(this);
    }

    /**
     * @return Information about this model
     */
    public Description getDescription() {
        return description;
    }

    /**
     * @return The individual parts in the model
     */
    public Bone[] getBones() {
        return bones;
    }

    @Override
    public String toString() {
        return "BedrockModel{" +
                "description=" + description +
                ", bones=" + Arrays.toString(bones) +
                '}';
    }

    /**
     * <p>The different types of polygons that can be represented by a poly mesh.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public enum PolyType {

        TRIANGLES("tri_list", 3), QUADS("quad_list", 4);

        private final String name;
        private final int vertices;

        PolyType(String name, int vertices) {
            this.name = name;
            this.vertices = vertices;
        }

        /**
         * @return The JSON name of this poly type
         */
        public String getName() {
            return name;
        }

        /**
         * @return The amount of vertices in this shape
         */
        public int getVertices() {
            return vertices;
        }
    }

    /**
     * <p>Information about the model.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Description {

        private final String identifier;
        private final float visibleBoundsWidth;
        private final float visibleBoundsHeight;
        private final Vector3f visibleBoundsOffset;
        private final int textureWidth;
        private final int textureHeight;
        private final boolean preserveModelPose2588;

        public Description(String identifier, float visibleBoundsWidth, float visibleBoundsHeight, Vector3f visibleBoundsOffset, int textureWidth, int textureHeight, boolean preserveModelPose2588) {
            this.identifier = identifier;
            this.visibleBoundsWidth = visibleBoundsWidth;
            this.visibleBoundsHeight = visibleBoundsHeight;
            this.visibleBoundsOffset = visibleBoundsOffset;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.preserveModelPose2588 = preserveModelPose2588;
        }

        /**
         * @return The identifier of this model. Used to refer to this geometry definition
         */
        public String getIdentifier() {
            return identifier;
        }

        /**
         * @return The width of the visibility bounding box
         */
        public float getVisibleBoundsWidth() {
            return visibleBoundsWidth;
        }

        /**
         * @return The height of the visibility bounding box
         */
        public float getVisibleBoundsHeight() {
            return visibleBoundsHeight;
        }

        /**
         * @return The offset of the visibility bounding box from the origin
         */
        public Vector3f getVisibleBoundsOffset() {
            return visibleBoundsOffset;
        }

        /**
         * @return The width of the texture in pixels
         */
        public int getTextureWidth() {
            return textureWidth;
        }

        /**
         * @return The height of the texture in pixels
         */
        public int getTextureHeight() {
            return textureHeight;
        }

        public boolean isPreserveModelPose2588() {
            return preserveModelPose2588;
        }

        @Override
        public String toString() {
            return "Description{" +
                    "identifier='" + identifier + '\'' +
                    ", visibleBoundsWidth=" + visibleBoundsWidth +
                    ", visibleBoundsHeight=" + visibleBoundsHeight +
                    ", visibleBoundsOffset=" + visibleBoundsOffset +
                    ", textureWidth=" + textureWidth +
                    ", textureHeight=" + textureHeight +
                    ", preserveModelPose2588=" + preserveModelPose2588 +
                    '}';
        }
    }

    /**
     * <p>A single bone equivalent to {@link net.minecraft.client.model.geom.ModelPart}.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Bone {

        private final String name;
        private final boolean reset2588;
        private final boolean neverRender2588;
        private final String parent;
        private final Vector3f pivot;
        private final Vector3f rotation;
        private final Vector3f bindPoseRotation2588;
        private final boolean mirror;
        private final float inflate;
        private final boolean debug;
        private final Cube[] cubes;
        private final Locator[] locators;
        private final PolyMesh polyMesh;

        public Bone(String name, boolean reset2588, boolean neverRender2588, @Nullable String parent, Vector3f pivot, Vector3f rotation, Vector3f bindPoseRotation2588, boolean mirror, float inflate, boolean debug, Cube[] cubes, Locator[] locators, @Nullable PolyMesh polyMesh) {
            this.name = name;
            this.reset2588 = reset2588;
            this.neverRender2588 = neverRender2588;
            this.parent = parent;
            this.pivot = pivot;
            this.rotation = rotation;
            this.bindPoseRotation2588 = bindPoseRotation2588;
            this.mirror = mirror;
            this.inflate = inflate;
            this.debug = debug;
            this.cubes = cubes;
            this.locators = locators;
            this.polyMesh = polyMesh;
        }

        /**
         * @return The identifier used when fetching this bone
         */
        public String getName() {
            return name;
        }

        public boolean isReset2588() {
            return reset2588;
        }

        public boolean isNeverRender2588() {
            return neverRender2588;
        }

        /**
         * @return The bone this bone is relative to
         */
        @Nullable
        public String getParent() {
            return parent;
        }

        /**
         * @return The position this bone pivots around
         */
        public Vector3f getPivot() {
            return pivot;
        }

        /**
         * @return The initial rotation of this bone in degrees
         */
        public Vector3f getRotation() {
            return rotation;
        }

        public Vector3f getBindPoseRotation2588() {
            return bindPoseRotation2588;
        }

        /**
         * @return Whether the cube should be mirrored along the un-rotated x-axis
         */
        public boolean isMirror() {
            return mirror;
        }

        /**
         * @return The amount to grow in all directions
         */
        public float getInflate() {
            return inflate;
        }

        public boolean isDebug() {
            return debug;
        }

        /**
         * @return The list of cubes associated with this bone
         */
        public Cube[] getCubes() {
            return cubes;
        }

        /**
         * @return The list of positions attached to this bone
         */
        public Locator[] getLocators() {
            return locators;
        }

        /**
         * @return The polygon mesh associated with the bone or <code>null</code> if there is no poly mesh for this type
         */
        @Nullable
        public PolyMesh getPolyMesh() {
            return polyMesh;
        }

        @Override
        public String toString() {
            return "Bone{" +
                    "name='" + name + '\'' +
                    ", reset2588=" + reset2588 +
                    ", neverRender2588=" + neverRender2588 +
                    ", parent='" + parent + '\'' +
                    ", pivot=" + pivot +
                    ", rotation" + rotation +
                    ", bindPoseRotation2588" + bindPoseRotation2588 +
                    ", mirror=" + mirror +
                    ", inflate=" + inflate +
                    ", debug=" + debug +
                    ", cubes=" + Arrays.toString(cubes) +
                    ", locators=" + Arrays.toString(locators) +
                    '}';
        }
    }

    /**
     * <p>A single box in the model.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Cube {

        private final Vector3f origin;
        private final Vector3f size;
        private final Vector3f rotation;
        private final Vector3f pivot;
        private final boolean overrideInflate;
        private final float inflate;
        private final boolean overrideMirror;
        private final boolean mirror;
        private final CubeUV[] uv;

        public Cube(Vector3f origin, Vector3f size, Vector3f rotation, Vector3f pivot, boolean overrideInflate, float inflate, boolean overrideMirror, boolean mirror, CubeUV[] uv) {
            this.origin = origin;
            this.size = size;
            this.rotation = rotation;
            this.pivot = pivot;
            this.overrideInflate = overrideInflate;
            this.inflate = inflate;
            this.overrideMirror = overrideMirror;
            this.mirror = mirror;
            Validate.isTrue(uv.length == Direction.values().length);
            this.uv = uv;
        }

        /**
         * @return The un-rotated lower corner of the cube
         */
        public Vector3f getOrigin() {
            return origin;
        }

        /**
         * @return The amount to extend beyond the origin
         */
        public Vector3f getSize() {
            return size;
        }

        /**
         * @return The amount in degrees to rotate around the pivot
         */
        public Vector3f getRotation() {
            return rotation;
        }

        /**
         * @return The position to pivot rotation around
         */
        public Vector3f getPivotX() {
            return pivot;
        }

        /**
         * @return Whether this inflate value should be used instead of the bone value
         */
        public boolean isOverrideInflate() {
            return overrideInflate;
        }

        /**
         * @return The amount to grow in all directions
         */
        public float getInflate() {
            return inflate;
        }

        /**
         * @return Whether this mirror value should be used instead of the bone value
         */
        public boolean isOverrideMirror() {
            return overrideMirror;
        }

        /**
         * @return Whether the cube should be mirrored along the un-rotated x-axis
         */
        public boolean isMirror() {
            return mirror;
        }

        /**
         * Fetches the uv for the specified face.
         *
         * @param direction The direction of the face to fetch
         * @return The uv for that face or <code>null</code> to skip that face
         */
        @Nullable
        public CubeUV getUV(Direction direction) {
            return this.uv[direction.get3DDataValue()];
        }

        /**
         * @return The uvs for all faces
         */
        public CubeUV[] getUVs() {
            return uv;
        }

        @Override
        public String toString() {
            return "Cube{" +
                    "origin=" + origin +
                    ", size=" + size +
                    ", rotation=" + rotation +
                    ", pivot=" + pivot +
                    ", overrideInflate=" + overrideInflate +
                    ", inflate=" + inflate +
                    ", overrideMirror=" + overrideMirror +
                    ", mirror=" + mirror +
                    ", northUV=" + getUV(Direction.NORTH) +
                    ", eastUV=" + getUV(Direction.EAST) +
                    ", southUV=" + getUV(Direction.SOUTH) +
                    ", westUV=" + getUV(Direction.WEST) +
                    ", upUV=" + getUV(Direction.UP) +
                    ", downUV=" + getUV(Direction.DOWN) +
                    '}';
        }
    }

    /**
     * <p>A single UV for a face on a cube.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class CubeUV {

        private final float u;
        private final float v;
        private final float uSize;
        private final float vSize;
        private final String materialInstance;

        public CubeUV(float u, float v, float uSize, float vSize, String materialInstance) {
            this.u = u;
            this.v = v;
            this.uSize = uSize;
            this.vSize = vSize;
            this.materialInstance = materialInstance;
        }

        /**
         * @return The u origin for this face
         */
        public float getU() {
            return u;
        }

        /**
         * @return The v origin for this face
         */
        public float getV() {
            return v;
        }

        /**
         * @return The x size of the texture selection box
         */
        public float getUSize() {
            return uSize;
        }

        /**
         * @return The y size of the texture selection box
         */
        public float getVSize() {
            return vSize;
        }

        /**
         * @return The material texture to use for this face
         */
        public String getMaterialInstance() {
            return materialInstance;
        }

        @Override
        public String toString() {
            return "CubeUV{" +
                    "uv=(" + u + "," + v + ")" +
                    ", uvSize=(" + uSize + "," + vSize + ")" +
                    ", materialInstance='" + materialInstance + '\'' +
                    '}';
        }
    }

    /**
     * <p>Polygon mesh information for a single bone.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class PolyMesh {

        private final boolean normalizedUvs;
        private final Vector3f[] positions;
        private final Vector3f[] normals;
        private final Vec2[] uvs;
        private final Poly[] polys;
        private final PolyType polyType;

        public PolyMesh(boolean normalizedUvs, Vector3f[] positions, Vector3f[] normals, Vec2[] uvs, Poly[] polys, PolyType polyType) {
            this.normalizedUvs = normalizedUvs;
            this.positions = positions;
            this.normals = normals;
            this.uvs = uvs;
            this.polys = polys;
            this.polyType = polyType;
        }

        /**
         * @return Whether UVs should be read from <code>0-1</code> or <code>0-textureWidth/textureHeight</code>
         */
        public boolean isNormalizedUvs() {
            return normalizedUvs;
        }

        /**
         * @return The positions of all vertices
         */
        public Vector3f[] getPositions() {
            return positions;
        }

        /**
         * @return The direction values
         */
        public Vector3f[] getNormals() {
            return normals;
        }

        /**
         * @return The UV values
         */
        public Vec2[] getUvs() {
            return uvs;
        }

        /**
         * @return The polys in the mesh
         */
        public Poly[] getPolys() {
            return polys;
        }

        /**
         * @return The type of polys in this mesh
         */
        public PolyType getPolyType() {
            return polyType;
        }

        @Override
        public String toString() {
            return "PolyMesh{" +
                    "normalizedUvs=" + normalizedUvs +
                    ", positions=" + Arrays.toString(positions) +
                    ", normals=" + Arrays.toString(normals) +
                    ", uvs=" + Arrays.toString(uvs) +
                    ", polys=" + Arrays.toString(polys) +
                    ", polyType=" + polyType +
                    '}';
        }

        public static class Deserializer implements JsonDeserializer<PolyMesh> {

            private static PolyType parseType(JsonElement json) throws JsonParseException {
                if (!json.isJsonPrimitive())
                    throw new JsonSyntaxException("Expected String, was " + GsonHelper.getType(json));
                for (PolyType polyType : PolyType.values())
                    if (polyType.name.equalsIgnoreCase(json.getAsString()))
                        return polyType;
                throw new JsonSyntaxException("Unsupported poly type: " + json.getAsString() + ". Supported poly types: " + Arrays.toString(Arrays.stream(PolyType.values()).map(PolyType::getName).toArray(String[]::new)));
            }

            // Figure out what kinds of polys are being used based on the length of the polys array
            private static PolyType parseType(Poly[] polys) {
                if (polys.length == 0)
                    return PolyType.TRIANGLES;
                return polys[0].getPositions().length == 3 ? PolyType.TRIANGLES : PolyType.QUADS;
            }

            private static <T> T[] parsePositions(JsonObject json, String name, int size, Function<Integer, T[]> arrayGenerator, Function<JsonArray, T> generator) throws JsonParseException {
                JsonArray positionsJson = GsonHelper.getAsJsonArray(json, name, null);
                if (positionsJson == null)
                    return arrayGenerator.apply(0);

                T[] positions = arrayGenerator.apply(positionsJson.size());
                for (int i = 0; i < positionsJson.size(); i++) {
                    JsonElement element = positionsJson.get(i);
                    if (!element.isJsonArray())
                        throw new JsonSyntaxException("Expected " + name + " to be a JsonArray, was " + GsonHelper.getType(element));

                    JsonArray array = element.getAsJsonArray();
                    if (array.size() != size)
                        throw new JsonParseException("Expected " + size + " " + name + " values, was " + array.size());

                    positions[i] = generator.apply(array);
                }

                return positions;
            }

            @Override
            public PolyMesh deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                boolean normalizedUvs = GsonHelper.getAsBoolean(jsonObject, "normalized_uvs", false);
                Vector3f[] positions = parsePositions(jsonObject, "positions", 3, Vector3f[]::new, j -> new Vector3f(j.get(0).getAsFloat(), j.get(1).getAsFloat(), j.get(2).getAsFloat()));
                Vector3f[] normals = parsePositions(jsonObject, "normals", 3, Vector3f[]::new, j -> new Vector3f(j.get(0).getAsFloat(), j.get(1).getAsFloat(), j.get(2).getAsFloat()));
                Vec2[] uvs = parsePositions(jsonObject, "uvs", 2, Vec2[]::new, j -> new Vec2(j.get(0).getAsFloat(), j.get(1).getAsFloat()));

                if (!jsonObject.has("polys"))
                    throw new JsonSyntaxException("Missing polys, expected to find a JsonArray or String");

                JsonElement polysJson = jsonObject.get("polys");
                if (!polysJson.isJsonArray() && !(polysJson.isJsonPrimitive() && polysJson.getAsJsonPrimitive().isString()))
                    throw new JsonSyntaxException("Expected polys to be a JsonArray or String, was " + GsonHelper.getType(polysJson));

                Poly[] polys = polysJson.isJsonArray() ? context.deserialize(polysJson, Poly[].class) : new Poly[0];
                PolyType polyType = polysJson.isJsonPrimitive() ? parseType(polysJson) : parseType(polys);

                for (Poly poly : polys) {
                    if (poly.getPositions().length != polyType.getVertices())
                        throw new JsonSyntaxException("Expected positions to be of length " + polyType.getVertices() + ". Was " + poly.getPositions().length);
                    if (poly.getNormals().length != polyType.getVertices())
                        throw new JsonSyntaxException("Expected normals to be of length " + polyType.getVertices() + ". Was " + poly.getPositions().length);
                    if (poly.getUVs().length != polyType.getVertices())
                        throw new JsonSyntaxException("Expected positions to be of length " + polyType.getVertices() + ". Was " + poly.getPositions().length);
                }

                return new PolyMesh(normalizedUvs, positions, normals, uvs, polys, polyType);
            }
        }
    }

    /**
     * <p>An indexed polygon in a {@link PolyMesh}.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Poly {

        private final int[] positions;
        private final int[] normals;
        private final int[] uvs;

        public Poly(int[] positions, int[] normals, int[] uvs) {
            this.positions = positions;
            this.normals = normals;
            this.uvs = uvs;
        }

        /**
         * @return The indexes for the first vertex
         */
        public int[] getPositions() {
            return positions;
        }

        /**
         * @return The indexes for the second vertex
         */
        public int[] getNormals() {
            return normals;
        }

        /**
         * @return The indexes for the third vertex
         */
        public int[] getUVs() {
            return uvs;
        }

        @Override
        public String toString() {
            return "Poly{" +
                    "positions=" + Arrays.toString(positions) +
                    ", normals=" + Arrays.toString(normals) +
                    ", uvs=" + Arrays.toString(uvs) +
                    '}';
        }

        public static class Deserializer implements JsonDeserializer<Poly> {

            private static int[] parseVertex(JsonElement element) throws JsonParseException {
                if (!element.isJsonArray())
                    throw new JsonSyntaxException("Expected vertex to be a JsonArray, was " + GsonHelper.getType(element));
                JsonArray array = element.getAsJsonArray();
                if (array.size() != 3)
                    throw new JsonParseException("Expected 3 vertex values, was " + array.size());
                return new int[]{array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt()};
            }

            @Override
            public Poly deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonArray jsonArray = json.getAsJsonArray();
                if (jsonArray.size() != 3 && jsonArray.size() != 4)
                    throw new JsonSyntaxException("Expected 3 or 4 index values, was " + jsonArray.size());

                int[] array1 = parseVertex(jsonArray.get(0));
                int[] array2 = parseVertex(jsonArray.get(1));
                int[] array3 = parseVertex(jsonArray.get(2));
                if (jsonArray.size() == 4) {
                    int[] array4 = parseVertex(jsonArray.get(3));
                    return new Poly(new int[]{array1[0], array2[0], array3[0], array4[0]}, new int[]{array1[1], array2[1], array3[1], array4[1]}, new int[]{array1[2], array2[2], array3[2], array4[2]});
                }
                return new Poly(new int[]{array1[0], array2[0], array3[0]}, new int[]{array1[1], array2[1], array3[1]}, new int[]{array1[2], array2[2], array3[2]});
            }
        }
    }

    /**
     * <p>A single marker position inside a bone.</p>
     *
     * @author Ocelot
     * @since 1.0.0
     * TODO redo when animations are added
     */
    public static class Locator {

        private final String identifier;
        private final Vector3f position;

        public Locator(String identifier, Vector3f position) {
            this.identifier = identifier;
            this.position = position;
        }

        /**
         * @return The identifying name of this locator
         */
        public String getIdentifier() {
            return identifier;
        }

        /**
         * @return The position of this locator
         */
        public Vector3f getPosition() {
            return position;
        }

        @Override
        public String toString() {
            return "Locator{" +
                    "identifier='" + identifier + '\'' +
                    ", position=" + position +
                    '}';
        }
    }
}
