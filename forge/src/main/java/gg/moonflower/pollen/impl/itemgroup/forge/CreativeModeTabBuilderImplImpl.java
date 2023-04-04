package gg.moonflower.pollen.impl.itemgroup.forge;

import net.minecraft.core.NonNullList;
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
        return new CreativeModeTab(String.format("%s.%s", name.getNamespace(), name.getPath())) {
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
