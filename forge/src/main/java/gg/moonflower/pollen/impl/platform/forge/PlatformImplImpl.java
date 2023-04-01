package gg.moonflower.pollen.impl.platform.forge;

import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PlatformImplImpl {

    public static boolean isProduction() {
        return FMLLoader.isProduction();
    }

    public static BlockableEventLoop<?> getGameExecutor() {
        return LogicalSidedProvider.WORKQUEUE.get(EffectiveSide.get());
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static boolean isClient() {
        return FMLLoader.getDist().isClient();
    }

    public static boolean isOptifineLoaded() {
        return isModLoaded("optifine");
    }
}
