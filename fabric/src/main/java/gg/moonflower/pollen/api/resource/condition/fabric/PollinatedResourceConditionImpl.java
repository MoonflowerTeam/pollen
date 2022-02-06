package gg.moonflower.pollen.api.resource.condition.fabric;

import gg.moonflower.pollen.api.resource.condition.PollinatedResourceConditionProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollinatedResourceConditionImpl {

    public static PollinatedResourceConditionProvider and(PollinatedResourceConditionProvider... values) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider FALSE() {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider TRUE() {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider not(PollinatedResourceConditionProvider value) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider or(PollinatedResourceConditionProvider... values) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider itemExists(ResourceLocation name) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider blockExists(ResourceLocation name) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider fluidExists(ResourceLocation name) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider itemTagPopulated(Tag.Named<Item> tag) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider blockTagPopulated(Tag.Named<Block> tag) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider fluidTagPopulated(Tag.Named<Fluid> tag) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider allModsLoaded(String... modIds) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }

    public static PollinatedResourceConditionProvider anyModsLoaded(String... modIds) {
        throw new UnsupportedOperationException("Recipe conditions are not supported on Fabric");
    }
}
