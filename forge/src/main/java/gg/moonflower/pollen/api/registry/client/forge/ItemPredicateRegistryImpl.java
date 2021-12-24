package gg.moonflower.pollen.api.registry.client.forge;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ItemPredicateRegistryImpl {
    public static void register(ResourceLocation id, ItemPropertyFunction function) {
        ItemProperties.registerGeneric(id, function);
    }

    public static void register(Item item, ResourceLocation id, ItemPropertyFunction function) {
        ItemProperties.register(item, id, function);
    }
}
