package gg.moonflower.pollen.api.registry.client.fabric;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class KeybindRegistryImpl {
    public static KeyMapping register(KeyMapping key) {
        return KeyBindingHelper.registerKeyBinding(key);
    }
}
