package gg.moonflower.pollen.api.resource.modifier.type;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.event.events.LootTableConstructingEvent;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifier;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Modifies existing advancements to add extra criteria, requirements, and rewards.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class LootModifier extends ResourceModifier<LootTableConstructingEvent.Context> {

    private static final Gson GSON = createLootTableSerializer().create();

    private final LootContextParamSet lootContextParamSet;
    private final LootPool[] addPools;
    private final LootItemFunction[] addFunctions;
    private final Map<Integer, List<LootPool>> injectPools;
    private final Map<Integer, Set<Integer>> removePoolEntries;
    private final Map<Integer, Set<Integer>> removePoolConditions;
    private final Map<Integer, Set<Integer>> removePoolFunctions;
    private final int[] removePools;
    private final int[] removeFunctions;

    public LootModifier(ResourceLocation id, ResourceLocation[] inject, int priority, LootContextParamSet lootContextParamSet, LootPool[] addPools, LootItemFunction[] addFunctions, Map<Integer, List<LootPool>> injectPools, Map<Integer, Set<Integer>> removePoolEntries, Map<Integer, Set<Integer>> removePoolConditions, Map<Integer, Set<Integer>> removePoolFunctions, int[] removePools, int[] removeFunctions) {
        super(id, inject, priority);
        this.lootContextParamSet = lootContextParamSet;
        this.addPools = addPools;
        this.addFunctions = addFunctions;
        this.injectPools = injectPools;
        this.removePoolEntries = removePoolEntries;
        this.removePoolConditions = removePoolConditions;
        this.removePoolFunctions = removePoolFunctions;
        this.removePools = removePools;
        this.removeFunctions = removeFunctions;
    }

    @ApiStatus.Internal
    @ExpectPlatform
    public static GsonBuilder createLootTableSerializer() {
        return Platform.error();
    }

    @ApiStatus.Internal
    @ExpectPlatform
    public static void initPool(JsonObject json) {
        Platform.error();
    }

    /**
     * @return A new loot modifier builder
     */
    public static LootModifier.Builder lootModifier() {
        return new LootModifier.Builder();
    }

    @Override
    public LootModifier.Builder deconstruct() {
        return new LootModifier.Builder(this.inject, this.priority, this.lootContextParamSet, this.addPools, this.addFunctions, this.injectPools, this.removePoolEntries, this.removePoolConditions, this.removePoolFunctions, this.removePools, this.removeFunctions);
    }

    @Override
    public void modify(LootTableConstructingEvent.Context resource) throws JsonParseException {
        if (this.lootContextParamSet != null)
            resource.setParamSet(this.lootContextParamSet);
        for (LootPool pool : this.addPools)
            resource.addPool(pool);
        for (LootItemFunction function : this.addFunctions)
            resource.addFunction(function);
        for (Map.Entry<Integer, List<LootPool>> entry : this.injectPools.entrySet())
            resource.insertPool(entry.getKey(), entry.getValue().toArray(new LootPool[0]));
        for (Map.Entry<Integer, Set<Integer>> entry : this.removePoolEntries.entrySet())
            for (int index : entry.getValue())
                resource.removeEntry(entry.getKey(), index);
        for (Map.Entry<Integer, Set<Integer>> entry : this.removePoolConditions.entrySet())
            for (int index : entry.getValue())
                resource.removeCondition(entry.getKey(), index);
        for (Map.Entry<Integer, Set<Integer>> entry : this.removePoolFunctions.entrySet())
            for (int index : entry.getValue())
                resource.removeFunction(entry.getKey(), index);
        for (int index : this.removePools)
            resource.removePool(index);
        for (int index : this.removeFunctions)
            resource.removeFunction(index);
    }

    @Override
    public ResourceModifierType getType() {
        return ResourceModifierManager.LOOT.get();
    }

    public static class Builder extends ResourceModifier.Builder<LootModifier, Builder> {

        private LootContextParamSet lootContextParamSet;
        private final List<LootPool> addPools;
        private final List<LootItemFunction> addFunctions;
        private final Map<Integer, List<LootPool>> injectPools;
        private final Map<Integer, Set<Integer>> removePoolEntries;
        private final Map<Integer, Set<Integer>> removePoolConditions;
        private final Map<Integer, Set<Integer>> removePoolFunctions;
        private final List<Integer> removePools;
        private final List<Integer> removeFunctions;

        private Builder(ResourceLocation[] inject, int priority, @Nullable LootContextParamSet lootContextParamSet, LootPool[] addPools, LootItemFunction[] addFunctions, Map<Integer, List<LootPool>> injectPools, Map<Integer, Set<Integer>> removePoolEntries, Map<Integer, Set<Integer>> removePoolConditions, Map<Integer, Set<Integer>> removePoolFunctions, int[] removePools, int[] removeFunctions) {
            super(inject, priority);
            this.lootContextParamSet = lootContextParamSet;
            this.addPools = new LinkedList<>(Arrays.asList(addPools));
            this.addFunctions = new LinkedList<>(Arrays.asList(addFunctions));
            this.injectPools = injectPools;
            this.removePoolEntries = removePoolEntries;
            this.removePoolConditions = removePoolConditions;
            this.removePoolFunctions = removePoolFunctions;
            this.removePools = IntStream.of(removePools).boxed().collect(Collectors.toCollection(LinkedList::new));
            this.removeFunctions = IntStream.of(removeFunctions).boxed().collect(Collectors.toCollection(LinkedList::new));
        }

        private Builder() {
            super();
            this.lootContextParamSet = null;
            this.addPools = new LinkedList<>();
            this.addFunctions = new LinkedList<>();
            this.injectPools = new HashMap<>();
            this.removePoolEntries = new HashMap<>();
            this.removePoolConditions = new HashMap<>();
            this.removePoolFunctions = new HashMap<>();
            this.removePools = new LinkedList<>();
            this.removeFunctions = new LinkedList<>();
        }

        /**
         * Sets the parameters for loot context.
         *
         * @param lootContextParamSet The new parameters set or <code>null</code> to not change anything
         */
        public Builder setLootContextParamSet(@Nullable LootContextParamSet lootContextParamSet) {
            this.lootContextParamSet = lootContextParamSet;
            return this;
        }

        /**
         * Adds a new pool to the loot table.
         *
         * @param pool The pool to add
         */
        public Builder addPool(LootPool pool) {
            this.addPools.add(pool);
            return this;
        }

        /**
         * Adds a new function to the loot table.
         *
         * @param function The function to apply
         */
        public Builder addFunction(LootItemFunction function) {
            this.addFunctions.add(function);
            return this;
        }

        /**
         * Merges the data from the specified pool into the pool of specified index if present.
         *
         * @param poolIndex The index of the pool to inject into
         * @param pool      The pool to add
         */
        public Builder injectPool(int poolIndex, LootPool pool) {
            this.injectPools.computeIfAbsent(poolIndex, __ -> new ArrayList<>()).add(pool);
            return this;
        }

        /**
         * Removes the specified entry from the specified pool.
         *
         * @param poolIndex  The index of the pool to remove from
         * @param entryIndex The index of the entry to remove
         */
        public Builder removePoolEntry(int poolIndex, int entryIndex) {
            this.removePoolEntries.computeIfAbsent(poolIndex, __ -> new HashSet<>()).add(entryIndex);
            return this;
        }

        /**
         * Removes the specified condition from the specified pool.
         *
         * @param poolIndex      The index of the pool to remove from
         * @param conditionEntry The index of the condition to remove
         */
        public Builder removePoolCondition(int poolIndex, int conditionEntry) {
            this.removePoolConditions.computeIfAbsent(poolIndex, __ -> new HashSet<>()).add(conditionEntry);
            return this;
        }

        /**
         * Removes the specified function from the specified pool.
         *
         * @param poolIndex     The index of the pool to remove from
         * @param functionEntry The index of the function to remove
         */
        public Builder removePoolFunction(int poolIndex, int functionEntry) {
            this.removePoolFunctions.computeIfAbsent(poolIndex, __ -> new HashSet<>()).add(functionEntry);
            return this;
        }

        /**
         * Removes the specified pool from the table.
         *
         * @param index The index of the pool to remove
         */
        public Builder removePool(int index) {
            this.removePools.add(index);
            return this;
        }

        /**
         * Removes the specified function from the table.
         *
         * @param index The index of the function to remove
         */
        public Builder removeFunction(int index) {
            this.removeFunctions.add(index);
            return this;
        }

        /**
         * Deserializes a new modifier from JSON.
         *
         * @param name            The name of the
         * @param serverResources The server resources instance
         * @param json            The JSON to deserialize
         * @param inject          The resources to inject into
         * @param priority        The priority of this injection over others
         * @return The deserialized builder
         */
        public static Builder fromJson(ResourceLocation name, ReloadableServerResources serverResources, JsonObject json, ResourceLocation[] inject, int priority) {
            initPool(json);
            LootPool[] addPools = json.has("addPools") ? GSON.fromJson(json.get("addPools"), LootPool[].class) : new LootPool[0];
            LootItemFunction[] addFunctions = json.has("addFunctions") ? GSON.fromJson(json.get("addFunctions"), LootItemFunction[].class) : new LootItemFunction[0];
            LootContextParamSet lootContextParamSet = null;
            if (json.has("type")) {
                String string = GsonHelper.getAsString(json, "type");
                lootContextParamSet = LootContextParamSets.get(new ResourceLocation(string));
            }

            Map<Integer, List<LootPool>> injectPools = new HashMap<>();
            if (json.has("injectPools")) {
                JsonArray injectPoolsJson = GsonHelper.getAsJsonArray(json, "injectPools");
                for (int i = 0; i < injectPoolsJson.size(); i++) {
                    JsonObject injectPoolJson = GsonHelper.convertToJsonObject(injectPoolsJson.get(i), "injectPools[" + i + "]");
                    int index = GsonHelper.getAsInt(injectPoolJson, "index");
                    injectPoolJson.addProperty("rolls", 0);
                    if (!injectPoolJson.has("entries"))
                        injectPoolJson.add("entries", new JsonArray());
                    injectPools.computeIfAbsent(index, __ -> new ArrayList<>()).add(GSON.fromJson(injectPoolJson, LootPool.class));
                }
            }

            Map<Integer, Set<Integer>> removePoolEntries = deserializeEntries(json, "removePoolEntries");
            Map<Integer, Set<Integer>> removePoolConditions = deserializeEntries(json, "removePoolConditions");
            Map<Integer, Set<Integer>> removePoolFunctions = deserializeEntries(json, "removePoolFunctions");

            int[] removePools = json.has("removePools") ? GSON.fromJson(json.get("removePools"), int[].class) : new int[0];
            int[] removeFunctions = json.has("removeFunctions") ? GSON.fromJson(json.get("removeFunctions"), int[].class) : new int[0];

            return new Builder(inject, priority, lootContextParamSet, addPools, addFunctions, injectPools, removePoolEntries, removePoolConditions, removePoolFunctions, removePools, removeFunctions);
        }

        private static Map<Integer, Set<Integer>> deserializeEntries(JsonObject json, String key) {
            Map<Integer, Set<Integer>> map = new HashMap<>();
            if (json.has(key)) {
                JsonArray removePoolEntriesJson = GsonHelper.getAsJsonArray(json, key);
                for (int i = 0; i < removePoolEntriesJson.size(); i++) {
                    JsonObject removePoolEntryJson = GsonHelper.convertToJsonObject(removePoolEntriesJson.get(i), key + "[" + i + "]");
                    int poolIndex = GsonHelper.getAsInt(removePoolEntryJson, "poolIndex");

                    if (!removePoolEntryJson.has("index"))
                        throw new JsonSyntaxException("Missing index, expected to find an Int or Int Array");

                    int[] indices;
                    if (removePoolEntryJson.isJsonArray()) {
                        JsonArray removePoolEntryJsonArray = removePoolEntryJson.getAsJsonArray("index");
                        indices = new int[removePoolEntryJsonArray.size()];
                        for (int j = 0; j < removePoolEntryJsonArray.size(); j++)
                            indices[j] = GsonHelper.convertToInt(removePoolEntryJsonArray.get(j), "index[" + j + "]");
                    } else {
                        indices = new int[]{GsonHelper.getAsInt(removePoolEntryJson, "index")};
                    }

                    for (int index : indices) {
                        if (!map.computeIfAbsent(poolIndex, __ -> new HashSet<>()).add(index))
                            throw new IllegalStateException("Duplicate index: pool " + poolIndex + "[" + index + "]");
                    }
                }
            }
            return map;
        }

        private static void serializeEntries(JsonObject json, String key, Map<Integer, Set<Integer>> map) {
            if (map.isEmpty())
                return;

            JsonArray removePoolEntriesJson = new JsonArray();
            for (Map.Entry<Integer, Set<Integer>> entry : map.entrySet()) {
                Set<Integer> indices = entry.getValue();
                if (indices.isEmpty())
                    continue;

                JsonObject removePoolEntryJson = new JsonObject();
                removePoolEntryJson.addProperty("poolIndex", entry.getKey());
                if (indices.size() > 1) {
                    JsonArray removePoolEntryJsonArray = new JsonArray();
                    indices.stream().sorted().forEach(removePoolEntryJsonArray::add);
                    removePoolEntryJson.add("index", removePoolEntryJsonArray);
                } else {
                    removePoolEntryJson.addProperty("index", Iterables.getFirst(indices, 0));
                }

                removePoolEntriesJson.add(json);
            }
            json.add(key, removePoolEntriesJson);
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        protected ResourceModifierType getType() {
            return ResourceModifierManager.LOOT.get();
        }

        @Override
        public LootModifier build(ResourceLocation id) {
            if (this.inject.isEmpty())
                throw new IllegalStateException("'inject' must be defined");
            return new LootModifier(id, this.inject.toArray(new ResourceLocation[0]), this.priority, this.lootContextParamSet, this.addPools.toArray(new LootPool[0]), this.addFunctions.toArray(new LootItemFunction[0]), this.injectPools, this.removePoolEntries, this.removePoolConditions, this.removePoolFunctions, this.removePools.stream().mapToInt(Integer::intValue).toArray(), this.removeFunctions.stream().mapToInt(Integer::intValue).toArray());
        }

        @Override
        protected void serializeProperties(JsonObject json) {
            if (!this.addPools.isEmpty())
                json.add("pools", GSON.toJsonTree(this.addPools));
            if (!this.addFunctions.isEmpty())
                json.add("functions", GSON.toJsonTree(this.addFunctions));

            if (this.lootContextParamSet != null) {
                ResourceLocation id = LootContextParamSets.getKey(this.lootContextParamSet);
                if (id != null)
                    json.addProperty("type", id.toString());
            }

            if (!this.injectPools.isEmpty()) {
                JsonArray injectPoolsJson = GsonHelper.getAsJsonArray(json, "injectPools");
                for (Map.Entry<Integer, List<LootPool>> entry : this.injectPools.entrySet()) {
                    int index = entry.getKey();
                    for (LootPool pool : entry.getValue()) {
                        JsonObject injectPoolJson = new JsonObject();
                        injectPoolJson.addProperty("index", index);
                        injectPoolJson.add("pool", GSON.toJsonTree(pool));
                        injectPoolsJson.add(injectPoolJson);
                    }
                }
                json.add("injectPools", injectPoolsJson);
            }

            serializeEntries(json, "removePoolEntries", this.removePoolEntries);
            serializeEntries(json, "removePoolConditions", this.removePoolConditions);
            serializeEntries(json, "removePoolFunctions", this.removePoolFunctions);

            if (!this.removePools.isEmpty()) {
                JsonArray removePoolsJson = new JsonArray();
                this.removePools.stream().sorted().forEach(removePoolsJson::add);
                json.add("removePools", removePoolsJson);
            }

            if (!this.removeFunctions.isEmpty()) {
                JsonArray removeFunctionsJson = new JsonArray();
                this.removeFunctions.stream().sorted().forEach(removeFunctionsJson::add);
                json.add("removeFunctions", removeFunctionsJson);
            }
        }
    }
}
