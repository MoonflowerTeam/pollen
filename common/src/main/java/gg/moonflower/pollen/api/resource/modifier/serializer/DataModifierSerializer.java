package gg.moonflower.pollen.api.resource.modifier.serializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;

/**
 * Creates a data modifier from JSON.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface DataModifierSerializer extends ModifierSerializer {

    ResourceModifier.Builder<?, ?> deserialize(ResourceLocation name, ReloadableServerResources serverResources, JsonObject json, ResourceLocation[] inject, int priority) throws JsonParseException;
}
