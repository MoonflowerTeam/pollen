package gg.moonflower.pollen.api.platform.forge;

import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PlatformImpl {

    public static boolean isProduction() {
        return FMLLoader.isProduction();
    }
}
