package gg.moonflower.pollen.pinwheel.api.common.particle.event;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.lang.reflect.Type;

public record SoundParticleEvent(ResourceLocation effect) implements ParticleEvent {

    @Override
    public void execute(Context context) {
        context.soundEffect(this.effect);
    }

    public static class Deserializer implements JsonDeserializer<SoundParticleEvent> {

        @Override
        public SoundParticleEvent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(json, "sound_effect");
            ResourceLocation effect = context.deserialize(jsonObject.get("event_name"), ResourceLocation.class);//GsonHelper.getAsString(jsonObject, "event_name");
            return new SoundParticleEvent(effect);
        }
    }
}
