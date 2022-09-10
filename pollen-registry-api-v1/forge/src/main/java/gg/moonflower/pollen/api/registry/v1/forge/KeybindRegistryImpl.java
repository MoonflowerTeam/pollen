package gg.moonflower.pollen.api.registry.v1.forge;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class KeybindRegistryImpl {

    public static KeyMapping register(KeyMapping key) {
        ClientRegistry.registerKeyBinding(key);
        return key;
    }
}
