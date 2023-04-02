package gg.moonflower.pollen.impl.platform;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Optional;

public interface SidedPlatformImpl {

    Optional<RecipeManager> getRecipeManager();
}
