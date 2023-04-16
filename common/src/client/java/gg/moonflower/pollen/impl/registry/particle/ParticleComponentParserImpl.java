package gg.moonflower.pollen.impl.registry.particle;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pinwheel.api.particle.ParticleComponentParser;
import gg.moonflower.pinwheel.api.particle.component.ParticleComponent;
import gg.moonflower.pollen.api.registry.particle.v1.BedrockParticleComponentType;
import gg.moonflower.pollen.api.registry.particle.v1.BedrockParticleComponents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class ParticleComponentParserImpl implements ParticleComponentParser {

    @Override
    public Map<String, ParticleComponent> deserialize(JsonObject json) throws JsonParseException {
        Map<String, ParticleComponent> components = new HashMap<>(json.size());
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            try {
                ResourceLocation id = new ResourceLocation(entry.getKey());
                BedrockParticleComponentType<?> type = BedrockParticleComponents.COMPONENTS.getRegistrar().get(id);
                if (type == null) {
                    throw new JsonSyntaxException("Unknown particle component: " + id);
                }

                components.put(entry.getKey(), type.dataFactory().create(entry.getValue()));
            } catch (Exception e) {
                throw new JsonSyntaxException("Invalid particle component: " + entry.getKey(), e);
            }
        }
        return Collections.unmodifiableMap(components);
    }
}
