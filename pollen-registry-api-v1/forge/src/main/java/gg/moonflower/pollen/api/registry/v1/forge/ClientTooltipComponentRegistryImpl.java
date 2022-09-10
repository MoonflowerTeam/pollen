package gg.moonflower.pollen.api.registry.v1.forge;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.MinecraftForgeClient;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.Internal
public class ClientTooltipComponentRegistryImpl {

    public static <T extends TooltipComponent> void register(Class<T> componentClass, Function<? super T, ? extends ClientTooltipComponent> factory) {
        MinecraftForgeClient.registerTooltipComponentFactory(componentClass, factory);
    }
}
