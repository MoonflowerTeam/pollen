package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Function;

public final class ClientTooltipComponentRegistry {

    private ClientTooltipComponentRegistry() {
    }

    @ExpectPlatform
    public static <T extends TooltipComponent> void register(Class<T> componentClass, Function<? super T, ? extends ClientTooltipComponent> factory) {
        Platform.error();
    }
}
