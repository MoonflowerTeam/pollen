package gg.moonflower.pollen.api.registry.v1.content;

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
public interface TagRegistry {

    /**
     * Binds a new tag to the specified registry.
     *
     * @param registry The registry to create a tag for
     * @param name     The name of the tag
     * @param <T>      The type of object the tag is for
     * @return A key for the tag
     */
    static <T> TagKey<T> bind(ResourceKey<? extends Registry<T>> registry, ResourceLocation name) {
        return TagKey.create(registry, name);
    }

    /**
     * Binds a new tag to the specified registry.
     *
     * @param registry The registry to create a tag for
     * @param name     The name of the tag
     * @param <T>      The type of object the tag is for
     * @return A key for the tag
     */
    static <T> TagKey<T> bind(Registry<T> registry, ResourceLocation name) {
        return TagKey.create(registry.key(), name);
    }

    /**
     * Binds a new tag to the specified registry.
     *
     * @param registry The registry to create a tag for
     * @param name     The name of the tag
     * @param <T>      The type of object the tag is for
     * @return A key for the tag
     */
    static <T> TagKey<T> bind(PollinatedRegistry<T> registry, ResourceLocation name) {
        return TagKey.create(registry.key(), name);
    }

    /**
     * Binds a new tag to the item registry.
     *
     * @param name The name of the tag
     * @return A key for the tag
     */
    static TagKey<Item> bindItem(ResourceLocation name) {
        return bind(Registry.ITEM_REGISTRY, name);
    }

    /**
     * Binds a new tag to the block registry.
     *
     * @param name The name of the tag
     * @return A key for the tag
     */
    static TagKey<Block> bindBlock(ResourceLocation name) {
        return bind(Registry.BLOCK_REGISTRY, name);
    }

    /**
     * Binds a new tag to the entityy registry.
     *
     * @param name The name of the tag
     * @return A key for the tag
     */
    static TagKey<EntityType<?>> bindEntityType(ResourceLocation name) {
        return bind(Registry.ENTITY_TYPE_REGISTRY, name);
    }

    /**
     * Binds a new tag to the fluid registry.
     *
     * @param name The name of the tag
     * @return A key for the tag
     */
    static TagKey<Fluid> bindFluid(ResourceLocation name) {
        return bind(Registry.FLUID_REGISTRY, name);
    }
}
