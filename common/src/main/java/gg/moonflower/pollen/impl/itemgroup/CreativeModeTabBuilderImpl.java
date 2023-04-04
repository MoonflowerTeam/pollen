package gg.moonflower.pollen.impl.itemgroup;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class CreativeModeTabBuilderImpl {

    @ExpectPlatform
    public static CreativeModeTab build(ResourceLocation name, Supplier<ItemStack> icon, BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay) {
        return Pollen.expect();
    }
}
