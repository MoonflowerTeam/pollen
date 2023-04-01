package gg.moonflower.pollen.impl.platform.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.thread.BlockableEventLoop;
import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

@ApiStatus.Internal
public class PlatformImplImpl {

    private static final FabricPlatformExecutor PLATFORM_EXECUTOR = ServiceLoader.load(FabricPlatformExecutor.class).findFirst().orElseGet(DefaultPlatformExecutor::new);

    public static boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static BlockableEventLoop<?> getGameExecutor() {
        return PLATFORM_EXECUTOR.getGameExecutor();
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    public static boolean isOptifineLoaded() {
        return isModLoaded("optifabric");
    }
}
