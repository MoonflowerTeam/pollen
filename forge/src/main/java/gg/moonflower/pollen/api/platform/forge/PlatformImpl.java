package gg.moonflower.pollen.api.platform.forge;

import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.api.util.forge.PollinatedModContainerImpl;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.ApiStatus;

import java.util.stream.Stream;

@ApiStatus.Internal
public class PlatformImpl {

    public static boolean isProduction() {
        return FMLLoader.isProduction();
    }

    public static BlockableEventLoop<?> getGameExecutor() {
        return LogicalSidedProvider.WORKQUEUE.get(EffectiveSide.get());
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static Stream<PollinatedModContainer> getMods() {
        return ModList.get().applyForEachModContainer(PollinatedModContainerImpl::new);
    }

    public static boolean isClient() {
        return FMLLoader.getDist().isClient();
    }

    public static boolean isOptifineLoaded() {
        return isModLoaded("optifine");
    }
}
