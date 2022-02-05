package gg.moonflower.pollen.api.resource.modifier.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifier;
import net.minecraft.resources.ResourceLocation;

/**
 * Creates a resource modifier from JSON.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface ResourceModifierSerializer extends ModifierSerializer {

    ResourceModifier.Builder<?, ?> deserialize(ResourceLocation name, JsonObject json, ResourceLocation[] inject, int priority) throws JsonParseException;
}
