package gg.moonflower.pollen.impl.platform;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public class InvalidSidedPlatformImpl implements SidedPlatformImpl {

    @Override
    public Optional<RegistryAccess> getRegistryAccess() {
        return Optional.empty();
    }

    @Override
    public Optional<RecipeManager> getRecipeManager() {
        return Optional.empty();
    }
}
