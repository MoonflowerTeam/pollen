package gg.moonflower.pollen.api.registry.resource;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
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

    @ExpectPlatform
    public static Tag.Named<Item> bindItem(ResourceLocation name) {
        return Platform.error();
    }

    @ExpectPlatform
    public static Tag.Named<Block> bindBlock(ResourceLocation name) {
        return Platform.error();
    }

    @ExpectPlatform
    public static Tag.Named<EntityType<?>> bindEntityType(ResourceLocation name) {
        return Platform.error();
    }

    @ExpectPlatform
    public static Tag.Named<Fluid> bindFluid(ResourceLocation name) {
        return Platform.error();
    }
}
