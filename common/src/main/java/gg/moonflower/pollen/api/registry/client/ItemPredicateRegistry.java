package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public final class ItemPredicateRegistry {

    private ItemPredicateRegistry() {
    }

    @ExpectPlatform
    public static void register(ResourceLocation id, ClampedItemPropertyFunction function) {
        Platform.error();
    }

    @ExpectPlatform
    public static void register(Item item, ResourceLocation id, ClampedItemPropertyFunction function) {
        Platform.error();
    }
}
