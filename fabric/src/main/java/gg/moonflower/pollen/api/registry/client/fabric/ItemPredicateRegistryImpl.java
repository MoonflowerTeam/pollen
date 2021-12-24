package gg.moonflower.pollen.api.registry.client.fabric;

import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ItemPredicateRegistryImpl {
    public static void register(ResourceLocation id, ItemPropertyFunction function) {
        FabricModelPredicateProviderRegistry.register(id, function);
    }

    public static void register(Item item, ResourceLocation id, ItemPropertyFunction function) {
        FabricModelPredicateProviderRegistry.register(item, id, function);
    }
}
