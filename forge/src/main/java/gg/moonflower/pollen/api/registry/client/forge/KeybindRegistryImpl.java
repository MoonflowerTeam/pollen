package gg.moonflower.pollen.api.registry.client.forge;

import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeybindRegistryImpl {

    private static final Set<KeyMapping> KEY_MAPPINGS = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void onEvent(RegisterKeyMappingsEvent event) {
        KEY_MAPPINGS.forEach(event::register);
    }

    public static KeyMapping register(KeyMapping key) {
        KEY_MAPPINGS.add(key);
        return key;
    }
}
