package gg.moonflower.pollen.api.client.util.fabric;

import net.fabricmc.fabric.impl.item.group.ItemGroupExtensions;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public class CreativeModeTabBuilderImpl {

    public static CreativeModeTab buildImpl(ResourceLocation name, Supplier<ItemStack> icon, BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay) {
        ((ItemGroupExtensions) CreativeModeTab.TAB_BUILDING_BLOCKS).fabric_expandArray();
        return new CreativeModeTab(CreativeModeTab.TABS.length - 1, String.format("%s.%s", name.getNamespace(), name.getPath())) {
            @Override
            public ItemStack makeIcon() {
                return icon.get();
            }

            @Override
            public void fillItemList(NonNullList<ItemStack> stacks) {
                if (stacksForDisplay != null) {
                    stacksForDisplay.accept(stacks, this);
                    return;
                }

                super.fillItemList(stacks);
            }
        };
    }
}
