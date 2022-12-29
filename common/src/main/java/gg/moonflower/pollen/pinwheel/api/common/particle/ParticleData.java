package gg.moonflower.pollen.pinwheel.api.common.particle;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.common.particle.event.ParticleEvent;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public record ParticleData(Description description, Map<String, Curve> curves, Map<String, ParticleEvent> events,
                           Map<String, JsonElement> components) {

    public static final ParticleData EMPTY = new ParticleData(new Description("empty", new ResourceLocation("missing"), "missing"), new HashMap<>(), new HashMap<>(), new HashMap<>());

    /**
     * The different types of curves for calculating particle variables.
     *
     * @author Ocelot
     * @since 1.6.0
     */
    public enum CurveType {

        LINEAR("linear"), BEZIER("bezier"), BEZIER_CHAIN("bezier_chain"), CATMULL_ROM("catmull_rom");

        private final String name;

        CurveType(String name) {
            this.name = name;
        }

        /**
         * @return The JSON name of this curve type
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Information about the particle.
     *
     * @param identifier The identifier of this model. Used to refer to this particle definition
     * @param material   The material from the JSON. Used to get the {@link GeometryModelTextureTable}
     * @param texture    The {@link GeometryModelTexture} to use from that texture table
     * @author Ocelot
     * @since 1.6.0
     */
    public record Description(String identifier, ResourceLocation material, String texture) {

        public static class Deserializer implements JsonDeserializer<Description> {

            @Override
            public Description deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "description");
                String identifier = GsonHelper.getAsString(jsonObject, "identifier");
                JsonObject basicRenderParameters = GsonHelper.getAsJsonObject(jsonObject, "basic_render_parameters");
                return new Description(identifier, context.deserialize(basicRenderParameters.get("material"), ResourceLocation.class), GsonHelper.getAsString(basicRenderParameters, "texture", "texture"));
            }
        }
    }

    /**
     * @param type            The type of curve to use
     * @param nodes           The node inputs
     * @param input           The value to use as input into nodes. For example, <code>v.particle_age/v.particle_lifetime</code> would result in an input from <code>0</code> to <code>1</code> over the particle's lifetime
     * @param horizontalRange The range input is mapped to. From <code>0</code> to this value. <b><i>Note: This field is considered deprecated and optional</i></b>
     */
    public record Curve(CurveType type, CurveNode[] nodes, MolangExpression input, MolangExpression horizontalRange) {

        @Override
        public String toString() {
            return "Curve[type=" + type
                    + ", nodes=" + Arrays.toString(nodes)
                    + ", input=" + input
                    + ", horizontalRange=" + horizontalRange
                    + "]";
        }

        public static class Deserializer implements JsonDeserializer<Curve> {

            private static CurveType parseType(JsonElement json) throws JsonParseException {
                if (!json.isJsonPrimitive())
                    throw new JsonSyntaxException("Expected String, was " + GsonHelper.getType(json));
                for (CurveType curveType : CurveType.values())
                    if (curveType.name.equalsIgnoreCase(json.getAsString()))
                        return curveType;
                throw new JsonSyntaxException("Unsupported curve type: " + json.getAsString() + ". Supported curve types: " + Arrays.stream(CurveType.values()).map(CurveType::getName).collect(Collectors.joining(", ")));
            }

            private static CurveNode[] parseNodes(JsonElement json, CurveType type) {
                if (json.isJsonArray()) {
                    if (type == CurveType.BEZIER_CHAIN)
                        throw new JsonSyntaxException("Bezier Chain expected JsonObject, was " + GsonHelper.getType(json));

                    JsonArray array = GsonHelper.convertToJsonArray(json, "nodes");
                    CurveNode[] nodes = new CurveNode[array.size()];
                    int offset = type == CurveType.CATMULL_ROM ? 1 : 0;
                    for (int i = 0; i < nodes.length; i++) {
                        float time = (float) Math.max(i - offset, 0) / (float) (nodes.length - offset * 2 - 1);
                        MolangExpression value = JSONTupleParser.parseExpression(array.get(i), "nodes[" + i + "]");
                        nodes[i] = new CurveNode(time, value);
                    }
                    return nodes;
                } else if (json.isJsonObject()) {
                    JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "nodes");
                    List<CurveNode> curveNodes = new ArrayList<>(jsonObject.entrySet().size());
                    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                        try {
                            float time = Float.parseFloat(entry.getKey());
                            JsonObject nodeJson = GsonHelper.convertToJsonObject(entry.getValue(), entry.getKey());

                            if (type == CurveType.BEZIER_CHAIN) {
                                boolean singleValue = nodeJson.has("value");
                                boolean singleSlope = nodeJson.has("slope");
                                if (singleValue && (nodeJson.has("left_value") || nodeJson.has("right_value"))) {
                                    throw new JsonSyntaxException("left_value and right_value must not be present with value");
                                }
                                if (singleSlope && (nodeJson.has("left_slope") || nodeJson.has("right_slope"))) {
                                    throw new JsonSyntaxException("left_slope and right_slope must not be present with slope");
                                }

                                MolangExpression leftValue = singleValue ? JSONTupleParser.parseExpression(nodeJson.get("value"), "value") : JSONTupleParser.parseExpression(nodeJson.get("left_value"), "left_value");
                                MolangExpression rightValue = singleValue ? leftValue : JSONTupleParser.parseExpression(nodeJson.get("right_value"), "right_value");
                                MolangExpression leftSlope = singleSlope ? JSONTupleParser.parseExpression(nodeJson.get("slope"), "slope") : JSONTupleParser.parseExpression(nodeJson.get("left_slope"), "left_slope");
                                MolangExpression rightSlope = singleSlope ? leftSlope : JSONTupleParser.parseExpression(nodeJson.get("right_slope"), "right_slope");
                                curveNodes.add(new BezierChainCurveNode(time, leftValue, rightValue, leftSlope, rightSlope));
                            } else {
                                MolangExpression value = JSONTupleParser.parseExpression(nodeJson.get("value"), "value");
                                curveNodes.add(new CurveNode(time, value));
                            }
                        } catch (NumberFormatException e) {
                            throw new JsonParseException("Failed to parse nodes at time '" + entry.getKey() + "'", e);
                        }
                    }
                    if (type == CurveType.BEZIER && curveNodes.size() != 4)
                        throw new JsonSyntaxException("Bezier expected 4 nodes, had " + curveNodes.size());
                    curveNodes.sort((a, b) -> Float.compare(a.getTime(), b.getTime()));
                    return curveNodes.toArray(CurveNode[]::new);
                }
                throw new JsonSyntaxException("Expected nodes to be a JsonArray or JsonObject, was " + GsonHelper.getType(json));
            }

            @Override
            public Curve deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "curve");
                CurveType type = parseType(jsonObject.get("type"));
                CurveNode[] curves = jsonObject.has("nodes") ? parseNodes(jsonObject.get("nodes"), type) : new CurveNode[0];
                MolangExpression input = JSONTupleParser.getExpression(jsonObject, "input", null);
                MolangExpression horizontalRange = JSONTupleParser.getExpression(jsonObject, "horizontal_range", () -> MolangExpression.of(1.0F));
                return new Curve(type, curves, input, horizontalRange);
            }
        }
    }

    /**
     * A node in a {@link Curve}. Used to define most types of curves.
     *
     * @since 1.6.0
     */
    public static class CurveNode {

        private final float time;
        private final MolangExpression value;

        public CurveNode(float time, MolangExpression value) {
            this.time = time;
            this.value = value;
        }

        public float getTime() {
            return time;
        }

        public MolangExpression getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CurveNode curveNode = (CurveNode) o;
            return Float.compare(curveNode.time, time) == 0 && value.equals(curveNode.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(time, value);
        }

        @Override
        public String toString() {
            return "CurveNode{" +
                    "time=" + time +
                    ", value=" + value +
                    '}';
        }
    }

    /**
     * A node in a {@link Curve}. Used to define bezier chains specifically.
     *
     * @since 1.6.0
     */
    public static class BezierChainCurveNode extends CurveNode {

        private final MolangExpression leftValue;
        private final MolangExpression rightValue;
        private final MolangExpression leftSlope;
        private final MolangExpression rightSlope;

        public BezierChainCurveNode(float time, MolangExpression leftValue, MolangExpression rightValue, MolangExpression leftSlope, MolangExpression rightSlope) {
            super(time, leftValue);
            this.leftValue = leftValue;
            this.rightValue = rightValue;
            this.leftSlope = leftSlope;
            this.rightSlope = rightSlope;
        }

        public MolangExpression getLeftValue() {
            return leftValue;
        }

        public MolangExpression getRightValue() {
            return rightValue;
        }

        public MolangExpression getLeftSlope() {
            return leftSlope;
        }

        public MolangExpression getRightSlope() {
            return rightSlope;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            BezierChainCurveNode that = (BezierChainCurveNode) o;
            return leftValue.equals(that.leftValue) && rightValue.equals(that.rightValue) && leftSlope.equals(that.leftSlope) && rightSlope.equals(that.rightSlope);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), leftValue, rightValue, leftSlope, rightSlope);
        }

        @Override
        public String toString() {
            return "BezierChainCurveNode{" +
                    "time=" + getTime() +
                    ", leftValue=" + leftValue +
                    ", rightValue=" + rightValue +
                    ", leftSlope=" + leftSlope +
                    ", rightSlope=" + rightSlope +
                    '}';
        }
    }

    public static class Deserializer implements JsonDeserializer<ParticleData> {

        @Override
        public ParticleData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            Description description = context.deserialize(jsonObject.get("description"), Description.class);

            ImmutableMap.Builder<String, Curve> curves = ImmutableMap.builder();
            if (jsonObject.has("curves")) {
                JsonObject curvesJson = GsonHelper.getAsJsonObject(jsonObject, "curves");
                for (Map.Entry<String, JsonElement> entry : curvesJson.entrySet()) {
                    String key = entry.getKey();
                    if (!key.startsWith("variable.") && !key.startsWith("v."))
                        throw new JsonSyntaxException(key + " is not a valid MoLang variable name");
                    curves.put(key, context.deserialize(entry.getValue(), Curve.class));
                }
            }

            ImmutableMap.Builder<String, ParticleEvent> events = ImmutableMap.builder();
            if (jsonObject.has("events")) {
                JsonObject eventsJson = GsonHelper.getAsJsonObject(jsonObject, "events");
                for (Map.Entry<String, JsonElement> entry : eventsJson.entrySet()) {
                    events.put(entry.getKey(), context.deserialize(entry.getValue(), ParticleEvent.class));
                }
            }

            ImmutableMap.Builder<String, JsonElement> components = ImmutableMap.builder();
            if (jsonObject.has("components")) {
                JsonObject eventsJson = GsonHelper.getAsJsonObject(jsonObject, "components");
                for (Map.Entry<String, JsonElement> entry : eventsJson.entrySet()) {
                    components.put(entry.getKey(), entry.getValue());
                }
            }

            return new ParticleData(description, curves.build(), events.build(), components.build());
        }
    }
}
