package gg.moonflower.pollen.impl.itemgroup.fabric;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public class CreativeModeTabBuilderImplImpl {

    public static CreativeModeTab build(ResourceLocation name, Supplier<ItemStack> icon, BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay) {
        return FabricItemGroupBuilder.create(name).appendItems(stacksForDisplay).icon(icon).build();
    }
}
