package gg.moonflower.pollen.impl.platform;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.ServiceLoader;

@ApiStatus.Internal
public class PlatformImpl {

    private static final SidedPlatformImpl CLIENT_PLATFORM = ServiceLoader.load(SidedPlatformImpl.class).findFirst().orElseGet(InvalidSidedPlatformImpl::new);

    private static MinecraftServer runningServer;

    @ExpectPlatform
    public static boolean isProduction() {
        return Pollen.expect();
    }

    @ExpectPlatform
    public static boolean isClient() {
        return Pollen.expect();
    }

    @ExpectPlatform
    public static boolean isOptifineLoaded() {
        return Pollen.expect();
    }

    @ExpectPlatform
    public static BlockableEventLoop<?> getGameExecutor() {
        return Pollen.expect();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        return Pollen.expect();
    }

    public static Optional<MinecraftServer> getRunningServer() {
        return Optional.ofNullable(runningServer);
    }

    public static Optional<RecipeManager> getRecipeManager() {
        if (runningServer != null) {
            return Optional.of(runningServer.getRecipeManager());
        }
        return isClient() ? CLIENT_PLATFORM.getRecipeManager() : Optional.empty();
    }

    public static void init() {
        LifecycleEvent.SERVER_BEFORE_START.register(server -> runningServer = server);
        LifecycleEvent.SERVER_STOPPED.register(server -> runningServer = null);
    }
}
