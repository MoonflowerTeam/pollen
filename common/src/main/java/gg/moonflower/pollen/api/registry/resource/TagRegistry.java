package gg.moonflower.pollen.api.registry.resource;

import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * Registers tags for all vanilla types.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class TagRegistry {

    private TagRegistry() {
    }

    public static <T> TagKey<T> bind(ResourceKey<? extends Registry<T>> registry, ResourceLocation name) {
        return TagKey.create(registry, name);
    }

    public static <T> TagKey<T> bind(Registry<T> registry, ResourceLocation name) {
        return TagKey.create(registry.key(), name);
    }

    public static <T> TagKey<T> bind(PollinatedRegistry<T> registry, ResourceLocation name) {
        return TagKey.create(registry.key(), name);
    }

    public static TagKey<Item> bindItem(ResourceLocation name) {
        return bind(Registry.ITEM_REGISTRY, name);
    }

    public static TagKey<Block> bindBlock(ResourceLocation name) {
        return bind(Registry.BLOCK_REGISTRY, name);
    }

    public static TagKey<EntityType<?>> bindEntityType(ResourceLocation name) {
        return bind(Registry.ENTITY_TYPE_REGISTRY, name);
    }

    public static TagKey<Fluid> bindFluid(ResourceLocation name) {
        return bind(Registry.FLUID_REGISTRY, name);
    }
}
