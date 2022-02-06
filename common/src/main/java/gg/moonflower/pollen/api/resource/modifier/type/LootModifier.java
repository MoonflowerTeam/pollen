package gg.moonflower.pollen.api.resource.modifier.type;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.api.event.events.LootTableConstructingEvent;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifier;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerResources;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.TagEntry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SetNbtFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Modifies existing advancements to add extra criteria, requirements, and rewards.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class LootModifier extends ResourceModifier<LootTableConstructingEvent.Context> {

    private static final Gson GSON = Deserializers.createLootTableSerializer().create();

    private final LootPool[] addPools;
    private final LootItemFunction[] addFunctions;

    @Nullable
    @ApiStatus.Internal
    public static ResourceLocation loadingId;

    public LootModifier(ResourceLocation id, ResourceLocation[] inject, int priority, LootPool[] addPools, LootItemFunction[] addFunctions) {
        super(id, inject, priority);
        this.addPools = addPools;
        this.addFunctions = addFunctions;
    }

    /**
     * @return A new loot modifier builder
     */
    public static LootModifier.Builder lootModifier() {
        return new LootModifier.Builder();
    }

    @Override
    public LootModifier.Builder deconstruct() {
        return new LootModifier.Builder(this.inject, this.priority, this.addPools, this.addFunctions);
    }

    @Override
    public void modify(LootTableConstructingEvent.Context resource) throws JsonParseException {
        for (LootPool pool : this.addPools)
            resource.addPool(pool);
        for (LootItemFunction function : this.addFunctions)
            resource.addFunction(function);
    }

    @Override
    public ResourceModifierType getType() {
        return ResourceModifierManager.LOOT.get();
    }

    public static class Builder extends ResourceModifier.Builder<LootModifier, Builder> {

        private final List<LootPool> addPools;
        private final List<LootItemFunction> addFunctions;

        private Builder(ResourceLocation[] inject, int priority, LootPool[] addPools, LootItemFunction[] addFunctions) {
            super(inject, priority);
            this.addPools = new LinkedList<>(Arrays.asList(addPools));
            this.addFunctions = new LinkedList<>(Arrays.asList(addFunctions));
        }

        private Builder() {
            super();
            this.addPools = new LinkedList<>();
            this.addFunctions = new LinkedList<>();
        }

        /**
         * Adds the specified items to the loot table.
         *
         * @param items The items to add
         */
        public Builder addItems(ItemLike... items) {
            LootPool.Builder pool = LootPool.lootPool();
            for (ItemLike item : items)
                pool.add(LootItem.lootTableItem(item));
            return this.addPool(pool.build());
        }

        /**
         * Adds the specified items to the loot table with a custom count.
         *
         * @param count The count to set the item to
         * @param items The items to add
         */
        public Builder addItems(NumberProvider count, ItemLike... items) {
            LootPool.Builder pool = LootPool.lootPool();
            for (ItemLike item : items)
                pool.add(LootItem.lootTableItem(item).apply(SetItemCountFunction.setCount(count)));
            return this.addPool(pool.build());
        }

        /**
         * Adds the specified items, counts, and tags to the loot table.
         *
         * @param stacks The stacks to add
         */
        public Builder addItems(ItemStack... stacks) {
            LootPool.Builder pool = LootPool.lootPool();
            for (ItemStack stack : stacks) {
                LootPoolSingletonContainer.Builder<?> builder = LootItem.lootTableItem(stack.getItem());
                if (stack.getCount() != 1)
                    builder.apply(SetItemCountFunction.setCount(ConstantValue.exactly(stack.getCount())));
                if (stack.hasTag())
                    builder.apply(SetNbtFunction.setTag(stack.getTag().copy()));
                pool.add(builder);
            }
            return this.addPool(pool.build());
        }

        /**
         * Adds all items from the specified tag to the loot table.
         *
         * @param tag The tag to get items from
         */
        public Builder addTag(Tag<Item> tag) {
            return this.addPool(LootPool.lootPool().add(TagEntry.expandTag(tag)).build());
        }

        /**
         * Adds all items from the specified tag to the loot table with a custom count.
         *
         * @param tag   The tag to get items from
         * @param count The count to set the items to
         */
        public Builder addTag(Tag<Item> tag, NumberProvider count) {
            return this.addPool(LootPool.lootPool().add(TagEntry.expandTag(tag).apply(SetItemCountFunction.setCount(count))).build());
        }

        /**
         * Adds a new pool to the loot table. Used for anything more complex than adding a single item or tag.
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
         * Deserializes a new modifier from JSON.
         *
         * @param name            The name of the
         * @param serverResources The server resources instance
         * @param json            The JSON to deserialize
         * @param inject          The resources to inject into
         * @param priority        The priority of this injection over others
         * @return The deserialized builder
         */
        public static Builder fromJson(ResourceLocation name, ServerResources serverResources, JsonObject json, ResourceLocation[] inject, int priority) {
            LootPool[] lootPools = json.has("pools") ? GSON.fromJson(json.get("pools"), LootPool[].class) : new LootPool[0];
            LootItemFunction[] lootItemFunctions = json.has("functions") ? GSON.fromJson(json.get("functions"), LootItemFunction[].class) : new LootItemFunction[0];
            return new Builder(inject, priority, lootPools, lootItemFunctions);
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
            return new LootModifier(id, this.inject.toArray(new ResourceLocation[0]), this.priority, this.addPools.toArray(new LootPool[0]), this.addFunctions.toArray(new LootItemFunction[0]));
        }

        @Override
        protected void serializeProperties(JsonObject json) {
            if (!this.addPools.isEmpty())
                json.add("pools", GSON.toJsonTree(this.addPools));
            if (!this.addFunctions.isEmpty())
                json.add("functions", GSON.toJsonTree(this.addFunctions));
        }
    }
}
