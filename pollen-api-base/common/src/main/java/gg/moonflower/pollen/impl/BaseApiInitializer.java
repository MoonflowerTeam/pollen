package gg.moonflower.pollen.impl;

import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class BaseApiInitializer {

    public static final String MOD_ID = "pollen-api-base";

    private static MinecraftServer server;

    public static void onCommonInit() {
        ServerLifecycleEvents.PRE_STARTING.register(server -> {
            BaseApiInitializer.server = server;
            return true;
        });
        ServerLifecycleEvents.STOPPED.register(server -> BaseApiInitializer.server = null);
    }

    @Nullable
    public static MinecraftServer getRunningServer() {
        return server;
    }
}
