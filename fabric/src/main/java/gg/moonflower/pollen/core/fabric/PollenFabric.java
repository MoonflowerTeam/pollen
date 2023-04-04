package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.config.v1.PollinatedConfigType;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.impl.config.fabric.ConfigTracker;
import gg.moonflower.pollen.impl.event.entity.ModifyTradesEventsImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PollenFabric implements ModInitializer {

    private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");

    private static Path getServerConfigPath(MinecraftServer server) {
        Path serverConfig = server.getWorldPath(SERVERCONFIG);
        if (!Files.isDirectory(serverConfig)) {
            try {
                Files.createDirectories(serverConfig);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create " + serverConfig, e);
            }
        }
        return serverConfig;
    }


    @Override
    public void onInitialize() {
        ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.COMMON, FabricLoader.getInstance().getConfigDir());
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.CLIENT, FabricLoader.getInstance().getConfigDir());

        Pollen.init();
        Pollen.postInit();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.SERVER, getServerConfigPath(server));
            ModifyTradesEventsImpl.init();
        });
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> ConfigTracker.INSTANCE.unloadConfigs(PollinatedConfigType.SERVER, getServerConfigPath(server)));
    }
}
