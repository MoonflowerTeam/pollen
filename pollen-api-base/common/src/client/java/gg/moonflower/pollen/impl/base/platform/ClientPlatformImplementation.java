package gg.moonflower.pollen.impl.base.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Optional;

public class ClientPlatformImplementation implements ClientPlatformService {

    @Override
    public Optional<RegistryAccess> getRegistryAccess() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null ? Optional.of(connection.registryAccess()) : Optional.empty();
    }

    @Override
    public Optional<RecipeManager> getRecipeManager() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        return connection != null ? Optional.of(connection.getRecipeManager()) : Optional.empty();
    }

    @Override
    public BlockableEventLoop<?> getExecutor() {
        return Minecraft.getInstance();
    }
}
