package gg.moonflower.pollen.api.registry.resource.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TagRegistryImpl {

    public static Tag.Named<Item> bindItem(ResourceLocation name) {
        return ItemTags.createOptional(name);
    }

    public static Tag.Named<Block> bindBlock(ResourceLocation name) {
        return BlockTags.createOptional(name);
    }

    public static Tag.Named<EntityType<?>> bindEntityType(ResourceLocation name) {
        return EntityTypeTags.createOptional(name);
    }

    public static Tag.Named<Fluid> bindFluid(ResourceLocation name) {
        return FluidTags.createOptional(name);
    }
}
