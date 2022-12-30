package gg.moonflower.pollen.api.base.platform.fabric;

import gg.moonflower.pollen.api.base.platform.PollinatedModContainer;
import gg.moonflower.pollen.impl.fabric.BaseApiInitializerFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.stream.Stream;

@ApiStatus.Internal
public class PlatformImpl {

    public static boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
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

    public static Optional<MinecraftServer> getRunningServer() {
        return Optional.ofNullable(BaseApiInitializerFabric.getServer());
    }
}
