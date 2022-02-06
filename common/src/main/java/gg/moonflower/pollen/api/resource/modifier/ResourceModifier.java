package gg.moonflower.pollen.api.resource.modifier;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modifies a loading resource before it is used.
 *
 * @param <T> The type of object to modify
 * @since 1.0.0
 */
public abstract class ResourceModifier<T> {

    protected final ResourceLocation id;
    protected final ResourceLocation[] inject;
    protected final int priority;

    protected ResourceModifier(ResourceLocation id, ResourceLocation[] inject, int priority) {
        this.id = id;
        this.inject = inject;
        this.priority = priority;
    }

    /**
     * Modifies the specified resources with this modifier's modifications.
     *
     * @param resource The resource to modify
     * @throws JsonParseException If this modifier is not valid for the specified resource
     */
    public abstract void modify(T resource) throws JsonParseException;

    /**
     * @return A builder that can be reconstructed into this modifier
     */
    public abstract Builder<?, ?> deconstruct();

    /**
     * @return The type of modifier this is
     */
    public abstract ResourceModifierType getType();

    /**
     * @return The id of this modifier
     */
    public ResourceLocation getId() {
        return id;
    }

    /**
     * @return The resources to inject into
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
        if (o == null || this.getClass() != o.getClass()) return false;
        ResourceModifier that = (ResourceModifier) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /**
     * @param <T> The type of modifier this builder is for
     * @param <V> The type of this builder
     */
    public abstract static class Builder<T extends ResourceModifier<?>, V> {

        protected final List<ResourceLocation> inject;
        protected int priority;

        protected Builder() {
            this.inject = new LinkedList<>();
            this.priority = 1000;
        }

        protected Builder(ResourceLocation[] inject, int priority) {
            this.inject = new LinkedList<>(Arrays.asList(inject));
            this.priority = priority;
        }

        protected abstract V getThis();

        protected abstract ResourceModifierType getType();

        /**
         * Adds a resource to inject into.
         *
         * @param id The id of the resource
         */
        public V injectInto(ResourceLocation id) {
            this.inject.add(id);
            return this.getThis();
        }

        /**
         * Sets the priority of this modifier. Lower injection priorities are applied first.
         *
         * @param priority The new priority of this modifier. By default, this is <code>1000</code>.
         */
        public V injectPriority(int priority) {
            this.priority = priority;
            return this.getThis();
        }

        /**
         * Constructs a new modifier with the specified id.
         *
         * @param id The id of the modifier to construct
         * @return A new modifier instance
         */
        public abstract T build(ResourceLocation id);

        /**
         * Saves this modifier into the specified consumer. Used for datagens.
         *
         * @param consumer The consumer to accept modifiers
         * @param id       The id of the modifier to construct
         * @return A new modifier instance
         */
        public T save(Consumer<T> consumer, ResourceLocation id) {
            T modifier = this.build(id);
            consumer.accept(modifier);
            return modifier;
        }

        /**
         * @return A JSON of all properties in this modifier
         */
        public final JsonObject serializeToJson() {
            if (this.inject.isEmpty())
                throw new IllegalStateException("'inject' must be defined");

            JsonObject json = new JsonObject();

            json.addProperty("type", String.valueOf(ResourceModifierManager.REGISTRY.getKey(this.getType())));

            if (this.inject.size() == 1) {
                json.addProperty("inject", this.inject.get(0).toString());
            } else {
                JsonArray injectJson = new JsonArray();
                for (ResourceLocation inject : this.inject)
                    injectJson.add(inject.toString());
                json.add("inject", injectJson);
            }

            if (this.priority != 1000)
                json.addProperty("priority", this.priority);

            this.serializeProperties(json);
            return json;
        }

        /**
         * @param json The json to add properties to
         */
        protected void serializeProperties(JsonObject json) {
        }
    }
}
