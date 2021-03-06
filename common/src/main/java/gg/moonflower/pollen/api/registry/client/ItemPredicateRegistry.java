package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ItemPredicateRegistry {

    private ItemPredicateRegistry() {
    }

    @ExpectPlatform
    public static void register(ResourceLocation id, ItemPropertyFunction function) {
        Platform.error();
    }

    @ExpectPlatform
    public static void register(Item item, ResourceLocation id, ItemPropertyFunction function) {
        Platform.error();
    }
}
