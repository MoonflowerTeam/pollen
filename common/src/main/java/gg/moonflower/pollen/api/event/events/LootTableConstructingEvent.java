package gg.moonflower.pollen.api.event.events;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.jetbrains.annotations.Nullable;

/**
 * Called for each new loot table deserialized from JSON.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface LootTableConstructingEvent {

    PollinatedEvent<LootTableConstructingEvent> EVENT = EventRegistry.create(LootTableConstructingEvent.class, events -> context -> {
        for (LootTableConstructingEvent event : events)
            event.modifyLootTable(context);
    });

    /**
     * @param context The context for deserialization
     */
    void modifyLootTable(Context context);

    /**
     * Context for deserializing a loot table.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    interface Context {

        /**
         * Adds a new loot pool to the table.
         *
         * @param lootPool The pool to add
         */
        void addPool(LootPool lootPool);

        /**
         * Adds a new item function to the table.
         *
         * @param function The function to add
         */
        void addFunction(LootItemFunction function);

        /**
         * Sets the parameter set to the specified value.
         *
         * @param parameterSet The new parameter set
         */
        void setParamSet(LootContextParamSet parameterSet);

        /**
         * @return The id of the loot table currently being modified or <code>null</code> if unknown
         */
        @Nullable
        ResourceLocation getId();

        /**
         * @return The loot table being modified
         */
        LootTable getLootTable();

        /**
         * @return The raw JSON the loot table came from
         */
        JsonObject getJson();

        /**
         * @return The JSON deserialization context
         */
        JsonDeserializationContext getJsonContext();
    }
}
