package gg.moonflower.pollen.api.registry.content.v1.fabric;

import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CompostablesRegistryImpl {

    public static void register(ItemLike item, float probability) {
        CompostingChanceRegistry.INSTANCE.add(item, probability);
    }
}
