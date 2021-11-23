package gg.moonflower.pollen.api.platform.forge;

import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.api.util.forge.PollinatedModContainerImpl;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.Executor;
import java.util.stream.Stream;

@ApiStatus.Internal
public class PlatformImpl {

    public static boolean isProduction() {
        return FMLLoader.isProduction();
    }

    public static Executor getGameExecutor() {
        return LogicalSidedProvider.WORKQUEUE.get(EffectiveSide.get());
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static Stream<PollinatedModContainer> getMods() {
        return ModList.get().applyForEachModContainer(PollinatedModContainerImpl::new);
    }
}
