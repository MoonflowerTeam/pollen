package gg.moonflower.pollen.api.platform.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.api.util.fabric.PollinatedModContainerImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;
import java.util.stream.Stream;

@ApiStatus.Internal
public class PlatformImpl {

    private static final Supplier<Supplier<BlockableEventLoop<?>>> CLIENT_EXECUTOR = () -> Minecraft::getInstance;

    public static boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static BlockableEventLoop<?> getGameExecutor() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? CLIENT_EXECUTOR.get().get() : Platform.getRunningServer().orElseThrow(IllegalStateException::new);
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static Stream<PollinatedModContainer> getMods() {
        return FabricLoader.getInstance().getAllMods().stream().map(PollinatedModContainerImpl::new);
    }

    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static boolean isOptifineLoaded() {
        return isModLoaded("optifabric");
    }
}
