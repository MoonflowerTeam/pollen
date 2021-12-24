package gg.moonflower.pollen.api.client.util;

import com.google.common.base.Suppliers;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Constructs new {@link CreativeModeTab} and allows the creation of manually sorted tabs using {@link #sorted(CreativeModeTab)}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class CreativeModeTabBuilder {

    private final ResourceLocation name;
    private Supplier<ItemStack> icon;
    private BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay;

    private CreativeModeTabBuilder(ResourceLocation name) {
        this.name = name;
        this.icon = () -> ItemStack.EMPTY;
        this.stacksForDisplay = null;
    }

    @ApiStatus.Internal
    @ExpectPlatform
    public static CreativeModeTab buildImpl(ResourceLocation name, Supplier<ItemStack> icon, BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay) {
        return Platform.error();
    }

    /**
     * Constructs a new tab builder.
     *
     * @param name The name to use for the translation key. It will be formatted as <code>namespace.path</code>
     * @return A new builder
     */
    public static CreativeModeTabBuilder builder(ResourceLocation name) {
        return new CreativeModeTabBuilder(name);
    }

    /**
     * Wraps the specified tab in a sorted tab.
     *
     * @param tab The tab to wrap
     * @return A new {@link SortedCreativeModeTab} that will reflect the original tab
     */
    public static SortedCreativeModeTab sorted(CreativeModeTab tab) {
        if (SortedCreativeModeTab.class.isAssignableFrom(tab.getClass()))
            throw new IllegalStateException("Tab is already sorted!");
        return new SortedCreativeModeTab(tab, tab.getId(), tab.getRecipeFolderName());
    }

    /**
     * Sets the icon of the tab.
     *
     * @param icon The new icon
     */
    public CreativeModeTabBuilder setIcon(Supplier<ItemStack> icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Sets the items that will appear in the tab.
     *
     * @param stacksForDisplay The consumer to add all items
     */
    public CreativeModeTabBuilder setItems(Consumer<List<ItemStack>> stacksForDisplay) {
        this.stacksForDisplay = (itemStacks, creativeModeTab) -> stacksForDisplay.accept(itemStacks);
        return this;
    }

    /**
     * Sets the items that will appear in the tab.
     *
     * @param stacksForDisplay The consumer to add all items
     */
    public CreativeModeTabBuilder setItems(BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay) {
        this.stacksForDisplay = stacksForDisplay;
        return this;
    }

    /**
     * @return A new creative mode tab
     */
    public CreativeModeTab build() {
        return buildImpl(this.name, this.icon, this.stacksForDisplay);
    }

    /**
     * Automatically indexes and sorts an item group by add order.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class SortedCreativeModeTab extends CreativeModeTab {

        private final CreativeModeTab parent;
        private final List<Supplier<? extends Item>> orderedItems;
        private final Supplier<Map<Item, Integer>> indexedItems;

        private SortedCreativeModeTab(CreativeModeTab parent, int index, String label) {
            super(index, label);
            this.parent = parent;
            this.orderedItems = new ArrayList<>();
            this.indexedItems = Suppliers.memoize(this::indexItems);
        }

        private Map<Item, Integer> indexItems() {
            Map<Item, Integer> indexMap = new HashMap<>();
            for (int i = 0; i < this.orderedItems.size(); i++)
                indexMap.put(this.orderedItems.get(i).get(), i);
            return indexMap;
        }

        private int getIndex(Item item) {
            Map<Item, Integer> indexes = this.indexedItems.get();
            return indexes.containsKey(item) ? indexes.get(item) : indexes.size();
        }

        @Override
        public ItemStack makeIcon() {
            return this.parent.makeIcon();
        }

        @Override
        public void fillItemList(NonNullList<ItemStack> items) {
            super.fillItemList(items);
            items.sort((stack1, stack2) ->
            {
                int index1 = this.getIndex(stack1.getItem());
                int index2 = this.getIndex(stack2.getItem());
                if (this.indexedItems.get().containsKey(stack1.getItem()) || this.indexedItems.get().containsKey(stack2.getItem()))
                    return Integer.compare(index1, index2); // Index by specified position

                return 1 + Registry.ITEM.getKey(stack1.getItem()).compareTo(Registry.ITEM.getKey(stack2.getItem())); // Index by registry name at end
            });
        }

        /**
         * @return The order items should be sorted in
         */
        public List<Supplier<? extends Item>> getOrderedItems() {
            return orderedItems;
        }
    }
}
