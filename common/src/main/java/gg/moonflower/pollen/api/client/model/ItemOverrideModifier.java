package gg.moonflower.pollen.api.client.model;

import com.google.gson.*;
import gg.moonflower.pollen.core.mixin.client.ItemOverrideDeserializerAccessor;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class ItemOverrideModifier {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ItemOverride.class, ItemOverrideDeserializerAccessor.invokeInit()).create();

    private final ResourceLocation id;
    private final ResourceLocation[] inject;
    private final int priority;
    private final List<ItemOverride> overrides;

    public ItemOverrideModifier(ResourceLocation id, ResourceLocation[] inject, int priority, List<ItemOverride> overrides) {
        this.id = id;
        this.inject = inject;
        this.priority = priority;
        this.overrides = overrides;
    }

    private static <T> T[] insert(T[] a, T[] b) {
        T[] expanded = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, expanded, a.length, b.length);
        return expanded;
    }

    private static <T> T[] remove(T[] array, T remove) {
        for (int i = 0; i < array.length; i++) {
            if (remove.equals(array[i])) {
                System.arraycopy(array, i + 1, array, i, array.length - i - 1); // Copy the rest of the array down
                array = Arrays.copyOf(array, array.length - 1); // Shrink array by 1
            }
        }
        return array;
    }

    /**
     * @return A new advancement modifier builder
     */
    public static Builder itemOverrideModifier() {
        return new Builder();
    }

    /**
     * @return A {@link Builder} that can be reconstructed into this modifier
     */
    public Builder deconstruct() {
        return new Builder(this.inject, this.priority, this.overrides);
    }

    /**
     * Modifies the specified advancement with this modifier's modifications.
     *
     * @param model The model to modify
     * @throws JsonParseException If this modifier is not valid for the specified advancement
     */
    public void modify(BlockModel model) throws JsonParseException {
        model.getOverrides().addAll(this.overrides);
    }

    /**
     * @return The id of this modifier
     */
    public ResourceLocation getId() {
        return this.id;
    }

    /**
     * @return The models to inject into
     */
    public ResourceLocation[] getInject() {
        return inject;
    }

    /**
     * @return The injection priority of this modifier. Lower injection priorities are applied first
     */
    public int getInjectPriority() {
        return priority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemOverrideModifier that = (ItemOverrideModifier) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public static class Builder {

        private final List<ResourceLocation> inject;
        private final List<ItemOverride> overrides;
        private int priority;

        private Builder(ResourceLocation[] inject, int priority, List<ItemOverride> overrides) {
            this.inject = new LinkedList<>(Arrays.asList(inject));
            this.priority = priority;
            this.overrides = overrides;
        }

        private Builder() {
            this.inject = new LinkedList<>();
            this.priority = 1000;
            this.overrides = new ArrayList<>();
        }

        private static <T> T[] getArray(JsonObject json, String name, T[] array, int minSize, Function<String, T> getter) {
            if (!json.has(name))
                return array;

            JsonArray jsonArray = GsonHelper.getAsJsonArray(json, name);
            if (jsonArray.size() < minSize)
                throw new JsonSyntaxException("Expected " + name + " to have at least " + minSize + "elements");
            if (array.length != jsonArray.size())
                array = Arrays.copyOf(array, jsonArray.size());

            for (int i = 0; i < jsonArray.size(); ++i)
                array[i] = getter.apply(GsonHelper.convertToString(jsonArray.get(i), name + "[" + i + "]"));

            return array;
        }

        /**
         * Deserializes a new modifier from JSON.
         *
         * @param json    The JSON to deserialize.
         * @return The deserialized builder
         */
        public static Builder fromJson(JsonObject json) {
            if (!json.has("inject"))
                throw new JsonSyntaxException("Missing inject, expected to find a String or JsonArray");

            JsonElement injectElement = json.get("inject");
            if (!(injectElement.isJsonPrimitive() && injectElement.getAsJsonPrimitive().isString()) && !injectElement.isJsonArray())
                throw new JsonSyntaxException("Expected inject to be a String or JsonArray, was " + GsonHelper.getType(injectElement));

            ResourceLocation[] inject = injectElement.isJsonPrimitive() && injectElement.getAsJsonPrimitive().isString() ? new ResourceLocation[]{new ResourceLocation(GsonHelper.convertToString(injectElement, "inject"))} : getArray(json, "inject", new ResourceLocation[0], 1, ResourceLocation::new);

            int priority = GsonHelper.getAsInt(json, "injectPriority", 1000);

            JsonArray overridesJson = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "overrides", new JsonArray()));
            List<ItemOverride> overrides = new ArrayList<>();
            for (int i = 0; i < overridesJson.size(); ++i) {
                overrides.add(GSON.fromJson(overridesJson.get(i), ItemOverride.class));
            }

            return new Builder(inject, priority, overrides);
        }

        /**
         * Adds an advancement to inject into.
         *
         * @param id The id of the advancement
         */
        public Builder injectInto(ResourceLocation id) {
            this.inject.add(id);
            return this;
        }

        /**
         * Sets the priority of this modifier. Lower injection priorities are applied first.
         *
         * @param priority The new priority of this modifier. By default, this is <code>1000</code>.
         */
        public Builder injectPriority(int priority) {
            this.priority = priority;
            return this;
        }

        /**
         * Adds the specified item override to this modifier.
         *
         * @param override The item override to add
         */
        public Builder override(ItemOverride override) {
            this.overrides.add(override);
            return this;
        }

        /**
         * Constructs a new modifier with the specified id.
         *
         * @param id The id of the modifier to construct
         * @return A new modifier instance
         */
        public ItemOverrideModifier build(ResourceLocation id) {
            if (this.inject.isEmpty())
                throw new IllegalStateException("'inject' must be defined");
            return new ItemOverrideModifier(id, this.inject.toArray(new ResourceLocation[0]), this.priority, this.overrides);
        }

        /**
         * Saves this modifier into the specified consumer. Used for datagens.
         *
         * @param consumer The consumer to accept modifiers
         * @param id       The id of the modifier to construct
         * @return A new modifier instance
         */
        public ItemOverrideModifier save(Consumer<ItemOverrideModifier> consumer, ResourceLocation id) {
            ItemOverrideModifier modifier = this.build(id);
            consumer.accept(modifier);
            return modifier;
        }

        /**
         * @return A JSON representing this modifier
         */
        public JsonObject serializeToJson() {
            if (this.inject.isEmpty())
                throw new IllegalStateException("'inject' must be defined");

            JsonObject jsonObject = new JsonObject();

            if (this.inject.size() == 1) {
                jsonObject.addProperty("inject", this.inject.get(0).toString());
            } else {
                JsonArray injectJson = new JsonArray();
                for (ResourceLocation inject : this.inject)
                    injectJson.add(inject.toString());
                jsonObject.add("inject", injectJson);
            }

            if (this.priority != 1000)
                jsonObject.addProperty("priority", this.priority);

            if (this.overrides.size() > 0) {
                JsonArray overridesJson = new JsonArray();
                for (ItemOverride override : this.overrides) {
                    overridesJson.add(GSON.toJson(override, ItemOverride.class));
                }
                jsonObject.add("overrides", overridesJson);
            }

            return jsonObject;
        }
    }
}
