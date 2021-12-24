package gg.moonflower.pollen.api.registry.resource.fabric;

import net.fabricmc.fabric.api.tag.TagFactory;
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
        return TagFactory.ITEM.create(name);
    }

    public static Tag.Named<Block> bindBlock(ResourceLocation name) {
        return TagFactory.BLOCK.create(name);
    }

    public static Tag.Named<EntityType<?>> bindEntityType(ResourceLocation name) {
        return TagFactory.ENTITY_TYPE.create(name);
    }

    public static Tag.Named<Fluid> bindFluid(ResourceLocation name) {
        return TagFactory.FLUID.create(name);
    }
}
