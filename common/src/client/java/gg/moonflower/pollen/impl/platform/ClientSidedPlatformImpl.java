package gg.moonflower.pollen.impl.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public class ClientSidedPlatformImpl implements SidedPlatformImpl {

    @Override
    public Optional<RecipeManager> getRecipeManager() {
        ClientPacketListener listener = Minecraft.getInstance().getConnection();
        return listener != null ? Optional.of(listener.getRecipeManager()) : Optional.empty();
    }
}
