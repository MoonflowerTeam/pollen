package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.fabric.ConfigTracker;
import gg.moonflower.pollen.api.event.EventListener;
import gg.moonflower.pollen.api.event.events.CommandRegistryEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvent;
import gg.moonflower.pollen.core.mixin.fabric.LevelResourceAccessor;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FabricEvents {

    private static final LevelResource SERVERCONFIG = LevelResourceAccessor.init("serverconfig");

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

    @EventListener
    public static void onEvent(ServerLifecycleEvent.Starting event) {
        ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.SERVER, getServerConfigPath(event.getServer()));
    }

    @EventListener
    public static void onEvent(ServerLifecycleEvent.Stopped event) {
        ConfigTracker.INSTANCE.unloadConfigs(PollinatedConfigType.SERVER, getServerConfigPath(event.getServer()));
    }

    @EventListener
    public static void onEvent(CommandRegistryEvent event) {
        ConfigCommand.register(event.getDispatcher(), event.getSelection() == Commands.CommandSelection.DEDICATED);
    }
}
