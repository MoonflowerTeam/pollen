package gg.moonflower.pollen.api.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.event.events.client.resource.ModelEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Manages all custom modifiers for item overrides.
 *
 * @author Jackson
 * @since 1.0.0
 */
public final class ItemOverrideModifierManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final PreparableReloadListener RELOAD_LISTENER = new Reloader();
    private static final Map<ResourceLocation, ItemOverrideModifier> ITEM_OVERRIDE_MODIFIERS = new HashMap<>();

    private ItemOverrideModifierManager() {
    }

    @ApiStatus.Internal
    public static void init() {
        ModelEvents.LOAD_BLOCK_MODEL.register((location, model) -> getModifiersFor(location).sorted(Comparator.comparing(ItemOverrideModifier::getInjectPriority).reversed()).forEachOrdered(modifier -> {
            try {
                modifier.modify(model);
            } catch (JsonParseException e) {
                LOGGER.error("Failed to apply item override modifier {}: {}", modifier.getId(), e.getMessage());
            }
        }));
    }

    @ApiStatus.Internal
    public static PreparableReloadListener getReloadListener() {
        return RELOAD_LISTENER;
    }

    /**
     * Retrieves a modifier with the specified id.
     *
     * @param id The id of the modifier to retrieve
     * @return The modifier with that id or <code>null</code> if there is no modifier with that id
     */
    @Nullable
    public static ItemOverrideModifier getModifier(ResourceLocation id) {
        return ITEM_OVERRIDE_MODIFIERS.get(id);
    }

    /**
     * Creates a stream of all modifiers that modify the model with the specified id.
     *
     * @param id The location of the model to modify
     * @return The modifiers for the model item overrides
     */
    public static Stream<ItemOverrideModifier> getModifiersFor(ResourceLocation id) {
        return ITEM_OVERRIDE_MODIFIERS.values().stream().filter(modifier -> ArrayUtils.contains(modifier.getInject(), id));
    }

    private static class Reloader extends SimpleJsonResourceReloadListener implements PreparableReloadListener {

        private static final Gson GSON = new GsonBuilder().create();

        private Reloader() {
            super(GSON, "item_override_modifiers");
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            Map<ResourceLocation, ItemOverrideModifier> modifiers = new HashMap<>();
            map.forEach((name, element) -> {
                try {
                    modifiers.put(name, ItemOverrideModifier.Builder.fromJson(GsonHelper.convertToJsonObject(element, "item_override")).build(name));
                } catch (IllegalArgumentException | JsonParseException e) {
                    LOGGER.error("Parsing error loading custom item override modifier {}: {}", name, e.getMessage());
                }
            });
            ITEM_OVERRIDE_MODIFIERS.clear();
            ITEM_OVERRIDE_MODIFIERS.putAll(modifiers);

            LOGGER.info("Loaded {} item override modifiers", ITEM_OVERRIDE_MODIFIERS.size());
        }
    }
}
