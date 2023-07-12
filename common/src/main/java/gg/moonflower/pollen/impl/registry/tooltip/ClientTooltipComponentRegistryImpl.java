package gg.moonflower.pollen.impl.registry.tooltip;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Function;

public class ClientTooltipComponentRegistryImpl {

    @ExpectPlatform
    public static <T extends TooltipComponent> void register(Class<T> componentClass, Function<? super T, ? extends ClientTooltipComponent> factory) {
        Pollen.expect();
    }
}
