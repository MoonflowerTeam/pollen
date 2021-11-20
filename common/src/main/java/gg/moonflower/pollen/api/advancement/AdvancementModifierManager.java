package gg.moonflower.pollen.api.advancement;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.event.EventDispatcher;
import gg.moonflower.pollen.api.event.events.AdvancementConstructingEvent;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;
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
 * Manages all custom modifiers for advancements.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class AdvancementModifierManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, AdvancementModifier> ADVANCEMENT_MODIFIERS = new HashMap<>();

    @ApiStatus.Internal
    public static void init() {
        EventDispatcher.register(AdvancementConstructingEvent.class, AdvancementModifierManager::modifyAdvancements);
    }

    @ApiStatus.Internal
    public static PreparableReloadListener createReloader(PredicateManager predicateManager) {
        return new Reloader(predicateManager);
    }

    private static void modifyAdvancements(AdvancementConstructingEvent event) {
        getModifiersFor(event.getContext().getAdvancementId()).sorted(Comparator.comparing(AdvancementModifier::getInjectPriority).reversed()).forEachOrdered(modifier -> {
            try {
                modifier.modify(event.getBuilder());
                System.out.println(modifier.deconstruct().serializeToJson());
            } catch (JsonParseException e) {
                LOGGER.error("Failed to apply advancement modifier {}: {}", modifier.getId(), e.getMessage());
            }
        });
    }

    /**
     * Retrieves a modifier with the specified id.
     *
     * @param id The id of the modifier to retrieve
     * @return The modifier with that id or <code>null</code> if there is no modifier with that id
     */
    @Nullable
    public static AdvancementModifier getModifier(ResourceLocation id) {
        return ADVANCEMENT_MODIFIERS.get(id);
    }

    /**
     * Creates a stream of all modifiers that modify the advancement with the specified id.
     *
     * @param id The id of the advancement to modify
     * @return The modifiers for that advancement
     */
    public static Stream<AdvancementModifier> getModifiersFor(ResourceLocation id) {
        return ADVANCEMENT_MODIFIERS.values().stream().filter(modifier -> ArrayUtils.contains(modifier.getInject(), id));
    }

    private static class Reloader extends SimpleJsonResourceReloadListener implements PreparableReloadListener {

        private static final Gson GSON = new GsonBuilder().create();
        private final PredicateManager predicateManager;

        private Reloader(PredicateManager predicateManager) {
            super(GSON, "advancement_modifiers");
            this.predicateManager = predicateManager;
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
            Map<ResourceLocation, AdvancementModifier> modifiers = new HashMap<>();
            map.forEach((name, element) -> {
                try {
                    modifiers.put(name, AdvancementModifier.Builder.fromJson(GsonHelper.convertToJsonObject(element, "advancement_modifier"), new DeserializationContext(name, this.predicateManager)).build(name));
                } catch (IllegalArgumentException | JsonParseException e) {
                    LOGGER.error("Parsing error loading custom advancement modifier {}: {}", name, e.getMessage());
                }
            });
            ADVANCEMENT_MODIFIERS.clear();
            ADVANCEMENT_MODIFIERS.putAll(modifiers);

            LOGGER.info("Loaded {} advancement modifiers", ADVANCEMENT_MODIFIERS.size());
        }
    }
}
