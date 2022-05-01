package gg.moonflower.pollen.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import io.github.ocelot.molangcompiler.api.MolangCompiler;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.exception.MolangException;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Parses tuple values from JSON.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class JSONTupleParser {

    private JSONTupleParser() {
    }

    public static <T> T[] insert(T[] a, T[] b) {
        T[] expanded = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, expanded, a.length, b.length);
        return expanded;
    }

    public static <T> T[] remove(T[] array, T remove) {
        for (int i = 0; i < array.length; i++) {
            if (remove.equals(array[i])) {
                System.arraycopy(array, i + 1, array, i, array.length - i - 1); // Copy the rest of the array down
                array = Arrays.copyOf(array, array.length - 1); // Shrink array by 1
            }
        }
        return array;
    }

    public static <T> T[] getArray(JsonObject json, String name, T[] array, int minSize, Function<String, T> getter) {
        if (!json.has(name))
            return array;

        JsonArray jsonArray = GsonHelper.getAsJsonArray(json, name);
        if (jsonArray.size() < minSize)
            throw new JsonSyntaxException("Expected " + name + " to have at least " + minSize + "elements");
        if (array.length != jsonArray.size())
            array = Arrays.copyOf(array, jsonArray.size());

        for (int i = 0; i < jsonArray.size(); ++i)
            array[i] = getter.apply(GsonHelper.convertToString(jsonArray.get(i), name + "[" + i + "]"));

        return array;
    }

    /**
     * Parses an array of floats from the specified JSON.
     *
     * @param json         The json to get the values from
     * @param name         The name of the tuple element
     * @param length       The number of values to parse
     * @param defaultValue The default value if not required or <code>null</code> to make it required
     * @return An array of values parsed
     * @throws JsonSyntaxException If there is improper syntax in the JSON structure
     */
    public static float[] getFloat(JsonObject json, String name, int length, @Nullable Supplier<float[]> defaultValue) throws JsonSyntaxException {
        if (!json.has(name) && defaultValue != null)
            return defaultValue.get();
        if (!json.has(name))
            throw new JsonSyntaxException("Expected " + name + " to be a JsonArray or JsonPrimitive, was " + GsonHelper.getType(json));
        if (json.get(name).isJsonPrimitive() && json.getAsJsonPrimitive(name).isString())
            throw new JsonSyntaxException("Molang expressions are not supported");
        if (json.get(name).isJsonArray()) {
            JsonArray vectorJson = json.getAsJsonArray(name);
            if (vectorJson.size() != 1 && vectorJson.size() != length)
                throw new JsonParseException("Expected 1 or " + length + " " + name + " values, was " + vectorJson.size());

            float[] values = new float[length];
            if (vectorJson.size() == 1) {
                Arrays.fill(values, GsonHelper.convertToFloat(vectorJson.get(0), name));
            } else {
                for (int i = 0; i < values.length; i++) {
                    values[i] = GsonHelper.convertToFloat(vectorJson.get(i), name + "[" + i + "]");
                }
            }

            return values;
        }
        if (json.get(name).isJsonPrimitive()) {
            JsonPrimitive valuePrimitive = json.getAsJsonPrimitive(name);
            if (valuePrimitive.isNumber()) {
                float[] values = new float[length];
                Arrays.fill(values, valuePrimitive.getAsFloat());
                return values;
            }
        }
        throw new JsonSyntaxException("Expected " + name + " to be a JsonArray or JsonPrimitive, was " + GsonHelper.getType(json));
    }

    /**
     * Parses an array of expressions from the specified JSON.
     *
     * @param json         The json to get the values from
     * @param name         The name of the tuple element
     * @param length       The number of values to parse
     * @param defaultValue The default value if not required or <code>null</code> to make it required
     * @return An array of values parsed
     * @throws JsonSyntaxException If there is improper syntax in the JSON structure
     */
    public static MolangExpression[] getExpression(JsonObject json, String name, int length, @Nullable Supplier<MolangExpression[]> defaultValue) throws JsonSyntaxException {
        try {
            if (!json.has(name) && defaultValue != null)
                return defaultValue.get();
            if (!json.has(name))
                throw new JsonSyntaxException("Expected " + name + " to be a JsonArray or JsonPrimitive, was " + GsonHelper.getType(json));
            if (json.get(name).isJsonArray()) {
                JsonArray vectorJson = json.getAsJsonArray(name);
                if (vectorJson.size() != 1 && vectorJson.size() != length)
                    throw new JsonParseException("Expected 1 or " + length + " " + name + " values, was " + vectorJson.size());

                MolangExpression[] values = new MolangExpression[length];
                if (vectorJson.size() == 1) {
                    JsonElement vectorElement = vectorJson.get(0);
                    if (!vectorElement.isJsonPrimitive())
                        throw new JsonSyntaxException("Expected " + name + " to be a Float or String, was " + GsonHelper.getType(vectorElement));

                    JsonPrimitive vectorPrimitive = vectorElement.getAsJsonPrimitive();
                    if (vectorPrimitive.isString()) {
                        Arrays.fill(values, MolangCompiler.compile(vectorPrimitive.getAsString()));
                    } else if (vectorPrimitive.isNumber()) {
                        Arrays.fill(values, MolangExpression.of(vectorPrimitive.getAsFloat()));
                    } else {
                        throw new JsonSyntaxException("Expected " + name + " to be a Float or String, was " + GsonHelper.getType(vectorElement));
                    }
                } else {
                    for (int i = 0; i < values.length; i++) {
                        JsonElement vectorElement = vectorJson.get(i);
                        if (!vectorElement.isJsonPrimitive())
                            throw new JsonSyntaxException("Expected " + name + "[" + i + "] to be a Float or String, was " + GsonHelper.getType(vectorElement));

                        JsonPrimitive vectorPrimitive = vectorElement.getAsJsonPrimitive();
                        if (vectorPrimitive.isString()) {
                            values[i] = MolangCompiler.compile(vectorPrimitive.getAsString());
                        } else if (vectorPrimitive.isNumber()) {
                            values[i] = MolangExpression.of(vectorPrimitive.getAsFloat());
                        } else {
                            throw new JsonSyntaxException("Expected " + name + "[" + i + "] to be a Float or String, was " + GsonHelper.getType(vectorElement));
                        }
                    }
                }

                return values;
            }
            if (json.get(name).isJsonPrimitive()) {
                JsonPrimitive valuePrimitive = json.getAsJsonPrimitive(name);
                if (valuePrimitive.isNumber()) {
                    MolangExpression[] values = new MolangExpression[length];
                    Arrays.fill(values, MolangExpression.of(valuePrimitive.getAsFloat()));
                    return values;
                }
                if (valuePrimitive.isString()) {
                    MolangExpression[] values = new MolangExpression[length];
                    Arrays.fill(values, MolangCompiler.compile(valuePrimitive.getAsString()));
                    return values;
                }
            }
        } catch (MolangException e) {
            throw new JsonParseException("Failed to compile MoLang expression", e);
        }
        throw new JsonSyntaxException("Expected " + name + " to be a JsonArray or JsonPrimitive, was " + GsonHelper.getType(json));
    }

    /**
     * Parses a single expression from the specified JSON.
     *
     * @param json         The json to get the values from
     * @param name         The name of the tuple element
     * @param defaultValue The default value if not required or <code>null</code> to make it required
     * @return An array of values parsed
     * @throws JsonSyntaxException If there is improper syntax in the JSON structure
     */
    public static MolangExpression getExpression(JsonObject json, String name, @Nullable Supplier<MolangExpression> defaultValue) throws JsonSyntaxException {
        try {
            if (!json.has(name) && defaultValue != null)
                return defaultValue.get();
            if (!json.has(name))
                throw new JsonSyntaxException("Expected " + name + " to be a Float or JsonPrimitive, was " + GsonHelper.getType(json));
            if (json.get(name).isJsonPrimitive()) {
                JsonPrimitive valuePrimitive = json.getAsJsonPrimitive(name);
                if (valuePrimitive.isNumber())
                    return MolangExpression.of(valuePrimitive.getAsFloat());
                if (valuePrimitive.isString())
                    return MolangCompiler.compile(valuePrimitive.getAsString());
            }
        } catch (MolangException e) {
            throw new JsonParseException("Failed to compile MoLang expression", e);
        }
        throw new JsonSyntaxException("Expected " + name + " to be a Float or JsonPrimitive, was " + GsonHelper.getType(json));
    }
}
