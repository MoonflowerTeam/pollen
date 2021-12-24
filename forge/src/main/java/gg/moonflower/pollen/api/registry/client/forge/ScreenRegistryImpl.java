package gg.moonflower.pollen.api.registry.client.forge;

import gg.moonflower.pollen.api.registry.client.ScreenRegistry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ScreenRegistryImpl {
    public static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void register(MenuType<M> type, ScreenRegistry.ScreenFactory<M, S> factory) {
        MenuScreens.register(type, factory::create);
    }
}
