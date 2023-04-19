package gg.moonflower.pollen.impl.registry.tooltip.forge;

import dev.architectury.platform.forge.EventBuses;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ClientTooltipComponentRegistryImplImpl {

    private static final Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> FACTORIES = new ConcurrentHashMap<>();

    static {
        EventBuses.onRegistered(Pollen.MOD_ID, bus -> bus.register(ClientTooltipComponentRegistryImplImpl.class));
    }

    @SubscribeEvent
    public static void event(RegisterClientTooltipComponentFactoriesEvent event) {
        FACTORIES.forEach(event::register);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TooltipComponent> void register(Class<T> componentClass, Function<? super T, ? extends ClientTooltipComponent> factory) {
        FACTORIES.put(componentClass, (Function<TooltipComponent, ClientTooltipComponent>) factory);
    }
}
