package gg.moonflower.pollen.api.registry.tooltip.v1;

import gg.moonflower.pollen.impl.registry.tooltip.ClientTooltipComponentRegistryImpl;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Function;

public interface ClientTooltipComponentRegistry {

    static <T extends TooltipComponent> void register(Class<T> componentClass, Function<? super T, ? extends ClientTooltipComponent> factory) {
        ClientTooltipComponentRegistryImpl.register(componentClass, factory);
    }
}
