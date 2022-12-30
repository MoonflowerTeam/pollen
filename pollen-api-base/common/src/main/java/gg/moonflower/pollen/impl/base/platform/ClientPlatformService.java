package gg.moonflower.pollen.impl.base.platform;

import net.minecraft.core.RegistryAccess;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public interface ClientPlatformService {

    Optional<RegistryAccess> getRegistryAccess();

    Optional<RecipeManager> getRecipeManager();

    BlockableEventLoop<?> getExecutor();
}
