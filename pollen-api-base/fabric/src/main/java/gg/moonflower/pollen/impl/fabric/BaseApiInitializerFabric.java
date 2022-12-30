package gg.moonflower.pollen.impl.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class BaseApiInitializerFabric implements ModInitializer {

    private static MinecraftServer server;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(server -> BaseApiInitializerFabric.server = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(__ -> BaseApiInitializerFabric.server = null);
    }

    @Nullable
    public static MinecraftServer getServer() {
        return server;
    }
}
