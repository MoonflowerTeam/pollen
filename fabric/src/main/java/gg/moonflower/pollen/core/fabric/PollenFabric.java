package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.fabric.ConfigTracker;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerInteractEvent;
import gg.moonflower.pollen.api.event.events.entity.player.server.ServerPlayerTrackingEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvent;
import gg.moonflower.pollen.api.event.events.registry.CommandRegistryEvent;
import gg.moonflower.pollen.api.event.events.world.ChunkEvents;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.common.trades.VillagerTradeManager;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.command.ConfigCommand;
import gg.moonflower.pollen.core.mixin.fabric.LevelResourceAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ApiStatus.Internal
public class PollenFabric implements ModInitializer {

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

    @Override
    public void onInitialize() {
        Pollen.init();

        ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.COMMON, FabricLoader.getInstance().getConfigDir());
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.CLIENT, FabricLoader.getInstance().getConfigDir());

        Pollen.PLATFORM.setup();

        ServerTickEvents.START_SERVER_TICK.register(level -> TickEvent.SERVER_PRE.invoker().tick());
        ServerTickEvents.END_SERVER_TICK.register(level -> TickEvent.SERVER_POST.invoker().tick());
        ServerTickEvents.START_WORLD_TICK.register(TickEvent.LEVEL_PRE.invoker()::tick);
        ServerTickEvents.END_WORLD_TICK.register(TickEvent.LEVEL_POST.invoker()::tick);

        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvent.STARTING.invoker()::starting);
        ServerLifecycleEvents.SERVER_STARTED.register(ServerLifecycleEvent.STARTED.invoker()::started);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerLifecycleEvent.STOPPING.invoker()::stopping);
        ServerLifecycleEvents.SERVER_STOPPED.register(ServerLifecycleEvent.STOPPED.invoker()::stopped);

        ServerChunkEvents.CHUNK_LOAD.register(ChunkEvents.LOAD.invoker()::load);
        ServerChunkEvents.CHUNK_UNLOAD.register(ChunkEvents.UNLOAD.invoker()::unload);

        UseItemCallback.EVENT.register(PlayerInteractEvent.RIGHT_CLICK_ITEM.invoker()::interaction);
        UseBlockCallback.EVENT.register(PlayerInteractEvent.RIGHT_CLICK_BLOCK.invoker()::interaction);
        AttackBlockCallback.EVENT.register(PlayerInteractEvent.LEFT_CLICK_BLOCK.invoker()::interaction);
        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> PlayerInteractEvent.RIGHT_CLICK_ENTITY.invoker().interaction(player, world, hand, entity));

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> CommandRegistryEvent.EVENT.invoker().registerCommands(dispatcher, dedicated ? Commands.CommandSelection.DEDICATED : Platform.getRunningServer().isPresent() ? Commands.CommandSelection.INTEGRATED : Commands.CommandSelection.ALL));

        EntityTrackingEvents.START_TRACKING.register((entity, player) -> ServerPlayerTrackingEvents.START_TRACKING_ENTITY.invoker().startTracking(player, entity));
        EntityTrackingEvents.STOP_TRACKING.register((entity, player) -> ServerPlayerTrackingEvents.STOP_TRACKING_ENTITY.invoker().stopTracking(player, entity));

        // Pollen Events
        ServerLifecycleEvent.STARTING.register(server -> {
            ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.SERVER, getServerConfigPath(server));
            VillagerTradeManager.init();
        });
        ServerLifecycleEvent.STOPPED.register(server -> ConfigTracker.INSTANCE.unloadConfigs(PollinatedConfigType.SERVER, getServerConfigPath(server)));
        CommandRegistryEvent.EVENT.register((dispatcher, selection) -> ConfigCommand.register(dispatcher, selection == Commands.CommandSelection.DEDICATED));
    }
}
