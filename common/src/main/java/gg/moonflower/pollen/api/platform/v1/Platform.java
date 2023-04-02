package gg.moonflower.pollen.api.platform.v1;

import gg.moonflower.pollen.impl.platform.PlatformImpl;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.Optional;

/**
 * Information about the current platform the mod is running in.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface Platform {

    /**
     * @return Whether this mod is running in a production environment
     */
    static boolean isProduction() {
        return PlatformImpl.isProduction();
    }

    /**
     * @return Whether the current environment is a client
     */
    static boolean isClient() {
        return PlatformImpl.isClient();
    }

    /**
     * @return Whether Optifine is breaking the game
     */
    static boolean isOptifineLoaded() {
        return PlatformImpl.isOptifineLoaded();
    }

    /**
     * @return The main game executor. This is the Minecraft Client or Server instance
     */
    static BlockableEventLoop<?> getGameExecutor() {
        return PlatformImpl.getGameExecutor();
    }

    /**
     * Checks to see if the specified mod is loaded.
     *
     * @param modId The id of the mod to check
     * @return Whether that mod is loaded
     */
    static boolean isModLoaded(String modId) {
        return PlatformImpl.isModLoaded(modId);
    }

    /**
     * @return The currently running Minecraft Server instance. This will not be present in a remote client level
     */
    static Optional<MinecraftServer> getRunningServer() {
        return PlatformImpl.getRunningServer();
    }

    /**
     * @return The recipe manager for the running server or client
     */
    static Optional<RecipeManager> getRecipeManager() {
        return PlatformImpl.getRecipeManager();
    }
}
