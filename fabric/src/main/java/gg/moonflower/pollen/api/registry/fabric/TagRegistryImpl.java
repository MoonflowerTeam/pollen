package gg.moonflower.pollen.api.registry.fabric;

import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TagRegistryImpl {

    public static Tag.Named<Item> bindItem(ResourceLocation name) {
        return (Tag.Named<Item>) TagRegistry.item(name);
    }

    public static Tag.Named<Block> bindBlock(ResourceLocation name) {
        return (Tag.Named<Block>) TagRegistry.block(name);
    }

    public static Tag.Named<EntityType<?>> bindEntityType(ResourceLocation name) {
        return (Tag.Named<EntityType<?>>) TagRegistry.entityType(name);
    }

    public static Tag.Named<Fluid> bindFluid(ResourceLocation name) {
        return (Tag.Named<Fluid>) TagRegistry.fluid(name);
    }
}
