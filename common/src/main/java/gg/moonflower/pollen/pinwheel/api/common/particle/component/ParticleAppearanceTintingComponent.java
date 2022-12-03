package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.common.particle.render.CustomParticleRenderProperties;
import io.github.ocelot.molangcompiler.api.MolangCompiler;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.client.Camera;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Component that specifies the color of a particle.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleAppearanceTintingComponent implements CustomParticleComponent, CustomParticleRenderComponent {

    private final ColorSupplier red;
    private final ColorSupplier green;
    private final ColorSupplier blue;
    private final ColorSupplier alpha;

    public ParticleAppearanceTintingComponent(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        if (!jsonObject.has("color"))
            throw new JsonSyntaxException("Missing color, expected to find a JsonObject, JsonArray, string, or float");

        JsonElement colorElement = jsonObject.get("color");
        if (colorElement.isJsonObject()) {
            JsonObject colorObject = colorElement.getAsJsonObject();
            if (!colorObject.has("gradient"))
                throw new JsonSyntaxException("Missing gradient, expected to find a JsonObject or JsonArray");

            MolangExpression interpolant = JSONTupleParser.getExpression(colorObject, "interpolant", null);

            JsonElement gradientElement = colorObject.get("gradient");
            if (gradientElement.isJsonObject()) {
                JsonObject gradientObject = gradientElement.getAsJsonObject();

                List<Pair<Float, ColorSupplier[]>> colors = new ArrayList<>(gradientObject.size());
                float[] times = new float[gradientObject.size()];
                int i = 0;
                for (Map.Entry<String, JsonElement> entry : gradientObject.entrySet()) {
                    try {
                        float time = Float.parseFloat(entry.getKey());
                        times[i++] = time;
                        colors.add(Pair.of(time, parseColor(entry.getValue(), entry.getKey())));
                    } catch (NumberFormatException e) {
                        throw new JsonSyntaxException("Invalid time: " + entry.getKey(), e);
                    }
                }
                colors.sort((a, b) -> Float.compare(a.getFirst(), b.getFirst()));

                this.red = ColorSupplier.gradient(interpolant, colors.stream().map(pair -> pair.getSecond()[0]).toArray(ColorSupplier[]::new), times);
                this.green = ColorSupplier.gradient(interpolant, colors.stream().map(pair -> pair.getSecond()[1]).toArray(ColorSupplier[]::new), times);
                this.blue = ColorSupplier.gradient(interpolant, colors.stream().map(pair -> pair.getSecond()[2]).toArray(ColorSupplier[]::new), times);
                this.alpha = ColorSupplier.gradient(interpolant, colors.stream().map(pair -> pair.getSecond()[3]).toArray(ColorSupplier[]::new), times);
            } else if (gradientElement.isJsonArray()) {
                JsonArray gradientArray = gradientElement.getAsJsonArray();

                int count = gradientArray.size();
                ColorSupplier[] colors = new ColorSupplier[count * 4];
                float[] times = new float[count];
                for (int i = 0; i < count; i++) {
                    JsonElement element = gradientArray.get(i);
                    times[i] = (float) i / (float) count;
                    ColorSupplier[] elementColors = parseColor(element, "gradient[" + i + "]");
                    colors[i] = elementColors[0];
                    colors[count + i] = elementColors[1];
                    colors[count * 2 + i] = elementColors[2];
                    colors[count * 3 + i] = elementColors[3];
                }

                this.red = ColorSupplier.gradient(interpolant, Arrays.copyOfRange(colors, 0, count), times);
                this.green = ColorSupplier.gradient(interpolant, Arrays.copyOfRange(colors, count, count * 2), times);
                this.blue = ColorSupplier.gradient(interpolant, Arrays.copyOfRange(colors, count * 2, count * 3), times);
                this.alpha = ColorSupplier.gradient(interpolant, Arrays.copyOfRange(colors, count * 3, count * 4), times);
            } else {
                throw new JsonSyntaxException("Expected gradient to be a JsonObject or JsonArray, was " + GsonHelper.getType(gradientElement));
            }
        } else if (colorElement.isJsonArray() || colorElement.isJsonPrimitive()) {
            ColorSupplier[] colors = parseColor(colorElement, "color");
            this.red = colors[0];
            this.green = colors[1];
            this.blue = colors[2];
            this.alpha = colors[3];
        } else {
            throw new JsonSyntaxException("Expected color to be a JsonObject, JsonArray, string, or float, was " + GsonHelper.getType(colorElement));
        }
    }

    private static ColorSupplier[] parseColor(@Nullable JsonElement json, String name) {
        if (json == null)
            throw new JsonSyntaxException("Missing " + name + ", expected to find a JsonArray, string, or float");
        if (json.isJsonArray()) {
            JsonArray colorJson = json.getAsJsonArray();
            if (colorJson.size() != 3 && colorJson.size() != 4)
                throw new JsonSyntaxException("Expected 3 or 4 elements in " + name + ", got " + colorJson.size());

            ColorSupplier[] colors = new ColorSupplier[4];
            if (colorJson.size() == 3) {
                colors[3] = ColorSupplier.constant(1.0F);
            }

            for (int i = 0; i < colorJson.size(); i++) {
                JsonElement colorElement = colorJson.get(i);
                if (!colorElement.isJsonPrimitive())
                    throw new JsonSyntaxException("Expected " + name + "[" + i + "] to be a string or float, was " + GsonHelper.getType(colorElement));
                if (colorElement.getAsJsonPrimitive().isNumber()) {
                    colors[i] = ColorSupplier.constant(GsonHelper.convertToFloat(colorElement, name + "[" + i + "]"));
                } else {
                    try {
                        colors[i] = ColorSupplier.molang(MolangCompiler.compile(GsonHelper.convertToString(colorElement, name + "[" + i + "]")));
                    } catch (Exception e) {
                        throw new JsonSyntaxException("Failed to parse " + name + "[" + i + "]", e);
                    }
                }
            }

            return colors;
        } else if (json.isJsonPrimitive()) {
            String value = GsonHelper.convertToString(json, name);
            if (!value.startsWith("#") || value.length() > 9)
                throw new JsonSyntaxException("Invalid hex color: " + value);
            try {
                int color = Integer.parseUnsignedInt(value.substring(1), 16);
                float red = (float) (color >> 16 & 0xFF) / 255F;
                float green = (float) (color >> 8 & 0xFF) / 255F;
                float blue = (float) (color & 0xFF) / 255F;
                float alpha = value.length() > 7 ? (float) (color >> 24 & 0xFF) / 255F : 1.0F;
                return new ColorSupplier[]{ColorSupplier.constant(red), ColorSupplier.constant(green), ColorSupplier.constant(blue), ColorSupplier.constant(alpha)};
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException("Invalid hex color: " + value, e);
            }
        }

        throw new JsonSyntaxException("Expected " + name + " to be a JsonArray or string, was " + GsonHelper.getType(json));
    }

    @Override
    public void tick(CustomParticle particle) {
    }

    @Override
    public void render(CustomParticle particle, Camera camera, float partialTicks) {
        CustomParticleRenderProperties properties = particle.getRenderProperties();
        if (properties != null) {
            properties.setRed(this.red.get(particle));
            properties.setGreen(this.green.get(particle));
            properties.setBlue(this.blue.get(particle));
            properties.setAlpha(this.alpha.get(particle));
        }
    }

    private interface ColorSupplier {
        float get(CustomParticle particle);

        static ColorSupplier constant(float value) {
            return particle -> value;
        }

        static ColorSupplier molang(MolangExpression component) {
            return particle -> component.safeResolve(particle.getRuntime());
        }

        static ColorSupplier gradient(MolangExpression interpolant, ColorSupplier[] colors, float[] times) {
            if (colors.length < 2 || colors.length != times.length)
                throw new IllegalArgumentException("Colors must equal times and have at least 2 for a gradient");
            return new Gradient(interpolant, colors, times);
        }
    }

    private record Gradient(MolangExpression interpolant, ColorSupplier[] colors,
                            float[] times) implements ColorSupplier {

        @Override
        public float get(CustomParticle particle) {
            float input = this.interpolant.safeResolve(particle.getRuntime());
            ColorSupplier start = this.colors[0];
            ColorSupplier end = this.colors[1];
            float startTime = this.times[0];
            float endTime = this.times[1];
            for (int i = 0; i < this.colors.length; i++) {
                if (this.times[i] <= input) {
                    start = this.colors[i];
                    end = start;
                    startTime = this.times[i];
                    endTime = 1.0F;
                } else {
                    end = this.colors[i];
                    endTime = this.times[i];
                    break;
                }
            }

            float a = start.get(particle);
            if (startTime == endTime)
                return a;

            float b = end.get(particle);
            return Mth.lerp((input - startTime) / (endTime - startTime), a, b);
        }
    }
}
