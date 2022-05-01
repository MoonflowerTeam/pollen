package gg.moonflower.pollen.api.platform.forge;

import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.api.util.forge.PollinatedModContainerImpl;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

@ApiStatus.Internal
public class PlatformImpl {

    private static final Set<String> SENSITIVE_ARGS = new HashSet<>(Arrays.asList(
            // all lowercase without --
            "accesstoken",
            "clientid",
            "profileproperties",
            "proxypass",
            "proxyuser",
            "username",
            "userproperties",
            "uuid",
            "xuid"));

    private static String[] arguments;

    public static String[] getLaunchArguments() {
        return arguments != null ? arguments : new String[0];
    }

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

    public static void setArguments(String[] arguments) {
        int length = 0;

        String[] result = Arrays.copyOf(arguments, arguments.length);
        for (String arg : arguments) {
            // Ignore all sensitive arguments
            if (!arg.startsWith("--") || !SENSITIVE_ARGS.contains(arg.substring(2).toLowerCase(Locale.ENGLISH))) {
                result[length++] = arg;
            }
        }

        if (length < result.length)
            result = Arrays.copyOf(result, length);

        PlatformImpl.arguments = result;
    }
}
