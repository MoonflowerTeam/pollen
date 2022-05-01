package gg.moonflower.pollen.api.registry.content.fabric;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FurnaceFuelRegistryImpl {

    public static void register(ItemLike item, int burnTicks) {
        FuelRegistry.INSTANCE.add(item, burnTicks);
    }
}
