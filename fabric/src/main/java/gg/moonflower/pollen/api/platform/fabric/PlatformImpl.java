package gg.moonflower.pollen.api.platform.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PlatformImpl {

    public static boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static BlockableEventLoop<?> getGameExecutor() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Minecraft.getInstance() : Platform.getRunningServer().orElseThrow(IllegalStateException::new);
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
