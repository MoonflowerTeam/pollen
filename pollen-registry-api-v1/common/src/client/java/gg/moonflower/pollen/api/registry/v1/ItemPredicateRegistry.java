package gg.moonflower.pollen.api.registry.v1;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public interface ItemPredicateRegistry {

    @ExpectPlatform
    static void register(ResourceLocation id, ClampedItemPropertyFunction function) {
        Platform.error();
    }

    @ExpectPlatform
    static void register(Item item, ResourceLocation id, ClampedItemPropertyFunction function) {
        Platform.error();
    }
}
