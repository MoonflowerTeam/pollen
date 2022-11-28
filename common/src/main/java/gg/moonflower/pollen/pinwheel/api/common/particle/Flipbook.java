package gg.moonflower.pollen.pinwheel.api.common.particle;

import com.google.gson.*;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public record Flipbook(MolangExpression baseU, MolangExpression baseV, MolangExpression sizeU, MolangExpression sizeV,
                       float stepU, float stepV, float fps, MolangExpression maxFrame, boolean stretchToLifetime,
                       boolean loop) {

    public static class Deserializer implements JsonDeserializer<Flipbook> {

        @Override
        public Flipbook deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            MolangExpression[] uv = JSONTupleParser.getExpression(jsonObject, "base_UV", 2, null);
            MolangExpression[] sizeUV = JSONTupleParser.getExpression(jsonObject, "size_UV", 2, null);
            float[] stepUV = JSONTupleParser.getFloat(jsonObject, "step_UV", 2, null);
            float fps = GsonHelper.getAsFloat(jsonObject, "frames_per_second", 1);
            MolangExpression maxFrame = JSONTupleParser.getExpression(jsonObject, "max_frame", null);
            boolean stretchToLifetime = GsonHelper.getAsBoolean(jsonObject, "stretch_to_lifetime", false);
            boolean loop = GsonHelper.getAsBoolean(jsonObject, "loop", false);
            return new Flipbook(uv[0], uv[1], sizeUV[0], sizeUV[1], stepUV[0], stepUV[1], fps, maxFrame, stretchToLifetime, loop);
        }
    }
}
