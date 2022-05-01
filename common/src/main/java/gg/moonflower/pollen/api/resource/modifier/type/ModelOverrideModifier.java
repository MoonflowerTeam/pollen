package gg.moonflower.pollen.api.resource.modifier.type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifier;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Modifies existing models to add extra overrides.
 *
 * @author Jackson
 * @author Ocelot
 * @since 1.0.0
 */
public class ModelOverrideModifier extends ResourceModifier<BlockModel> {

    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(ItemOverride.class, new ItemOverride.Deserializer()).create();

    private final ItemOverride[] overrides;

    public ModelOverrideModifier(ResourceLocation id, ResourceLocation[] inject, int priority, ItemOverride[] overrides) {
        super(id, inject, priority);
        this.overrides = overrides;
    }

    /**
     * @return A new item override modifier builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public Builder deconstruct() {
        return new Builder(this.inject, this.priority, this.overrides);
    }

    @Override
    public ResourceModifierType getType() {
        return ResourceModifierManager.MODEL_OVERRIDE.get();
    }

    @Override
    public void modify(BlockModel model) throws JsonParseException {
        model.getOverrides().addAll(Arrays.asList(this.overrides));
    }

    public static class Builder extends ResourceModifier.Builder<ModelOverrideModifier, Builder> {

        private final List<ItemOverride> overrides;

        private Builder(ResourceLocation[] inject, int priority, ItemOverride[] overrides) {
            super(inject, priority);
            this.overrides = new LinkedList<>(Arrays.asList(overrides));
        }

        private Builder() {
            super();
            this.overrides = new ArrayList<>();
        }

        @Override
        protected Builder getThis() {
            return this;
        }

        @Override
        protected ResourceModifierType getType() {
            return ResourceModifierManager.MODEL_OVERRIDE.get();
        }

        /**
         * Deserializes a new modifier from JSON.
         *
         * @param json The JSON to deserialize.
         * @return The deserialized builder
         */
        public static Builder fromJson(ResourceLocation name, JsonObject json, ResourceLocation[] inject, int priority) {
            JsonArray overridesJson = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "overrides", new JsonArray()));
            ItemOverride[] overrides = new ItemOverride[overridesJson.size()];
            for (int i = 0; i < overridesJson.size(); ++i)
                overrides[i] = GSON.fromJson(overridesJson.get(i), ItemOverride.class);
            return new Builder(inject, priority, overrides);
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

        @Override
        public ModelOverrideModifier build(ResourceLocation id) {
            if (this.inject.isEmpty())
                throw new IllegalStateException("'inject' must be defined");
            return new ModelOverrideModifier(id, this.inject.toArray(new ResourceLocation[0]), this.priority, this.overrides.toArray(new ItemOverride[0]));
        }

        @Override
        protected void serializeProperties(JsonObject json) {
            if (!this.overrides.isEmpty()) {
                JsonArray overridesJson = new JsonArray();
                for (ItemOverride override : this.overrides)
                    overridesJson.add(GSON.toJson(override, ItemOverride.class));
                json.add("overrides", overridesJson);
            }
        }
    }
}
