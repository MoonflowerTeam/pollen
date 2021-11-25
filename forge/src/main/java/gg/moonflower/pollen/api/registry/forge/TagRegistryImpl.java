package gg.moonflower.pollen.api.registry.forge;

import net.minecraft.tags.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TagRegistryImpl {

    public static Tag.Named<Item> bindItem(String name) {
        return ItemTags.bind(name);
    }

    public static Tag.Named<Block> bindBlock(String name) {
        return BlockTags.bind(name);
    }

    public static Tag.Named<EntityType<?>> bindEntityType(String name) {
        return EntityTypeTags.bind(name);
    }

    public static Tag.Named<Fluid> bindFluid(String name) {
        return FluidTags.bind(name);
    }
}
