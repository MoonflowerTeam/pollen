package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.registry.TagRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TagRegistryImpl {

    public static Tag.Named<Item> bindItem(String name) {
        return TagRegistry.bindItem(name);
    }

    public static Tag.Named<Block> bindBlock(String name) {
        return TagRegistry.bindBlock(name);
    }

    public static Tag.Named<EntityType<?>> bindEntityType(String name) {
        return TagRegistry.bindEntityType(name);
    }

    public static Tag.Named<Fluid> bindFluid(String name) {
        return TagRegistry.bindFluid(name);
    }
}
