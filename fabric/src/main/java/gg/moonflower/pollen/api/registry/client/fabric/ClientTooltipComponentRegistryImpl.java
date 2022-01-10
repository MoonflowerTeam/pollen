package gg.moonflower.pollen.api.registry.client.fabric;

import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@ApiStatus.Internal
public class ClientTooltipComponentRegistryImpl {

    private static final Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> FACTORIES = new ConcurrentHashMap<>();

    static {
        TooltipComponentCallback.EVENT.register(component -> {
            if (!FACTORIES.containsKey(component.getClass()))
                return null;
            return FACTORIES.get(component.getClass()).apply(component);
        });
    }

    @SuppressWarnings("unchecked")
    public static <T extends TooltipComponent> void register(Class<T> componentClass, Function<? super T, ? extends ClientTooltipComponent> factory) {
        FACTORIES.put(componentClass, (Function<TooltipComponent, ClientTooltipComponent>) factory);
    }
}
