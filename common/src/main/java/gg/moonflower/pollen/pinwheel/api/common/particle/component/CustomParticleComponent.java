package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleManager;
import net.minecraft.util.GsonHelper;
import org.jetbrains.annotations.Nullable;

/**
 * A component for custom particles.
 *
 * @author Ocelot
 * @see CustomParticleManager
 * @since 1.6.0
 */
public interface CustomParticleComponent {

    // TODO motion parametric

    /**
     * Called every tick to update this component.
     *
     * @param particle The particle to tick
     */
    void tick(CustomParticle particle);

    /**
     * Reads all event references in the specified json.
     *
     * @param json The json to read references from
     * @param name The name of the element to get
     * @return The events parsed
     * @throws JsonSyntaxException If the file is malformed
     */
    static String[] getEvents(JsonObject json, String name) throws JsonSyntaxException {
        if (!json.has(name))
            return new String[0];
        return parseEvents(json.get(name), name);
    }

    /**
     * Reads all event references in the specified json.
     *
     * @param element The element to get as events
     * @param name    The name of the element
     * @return The events parsed
     * @throws JsonSyntaxException If the file is malformed
     */
    static String[] parseEvents(@Nullable JsonElement element, String name) throws JsonSyntaxException {
        if (element == null)
            throw new JsonSyntaxException("Missing " + name + ", expected to find a JsonArray or string");
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            String[] events = new String[array.size()];
            for (int i = 0; i < array.size(); i++)
                events[i] = GsonHelper.convertToString(array.get(i), name + "[" + i + "]");
            return events;
        } else if (element.isJsonPrimitive()) {
            return new String[]{GsonHelper.convertToString(element, name)};
        }
        throw new JsonSyntaxException("Expected " + name + " to be a JsonArray or string, was " + GsonHelper.getType(element));
    }
}
