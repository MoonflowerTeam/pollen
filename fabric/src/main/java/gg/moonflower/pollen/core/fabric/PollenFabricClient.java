package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.event.events.CommandRegistryEvent;
import gg.moonflower.pollen.api.event.events.client.render.ReloadRendersEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvent;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvent;
import gg.moonflower.pollen.api.platform.Platform;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> EventDispatcher.post(new TickEvent.ClientEvent.Pre()));
        ClientTickEvents.END_CLIENT_TICK.register(client -> EventDispatcher.post(new TickEvent.ClientEvent.Post()));
        ClientTickEvents.START_WORLD_TICK.register(level -> EventDispatcher.post(new TickEvent.LevelEvent.Pre(level)));
        ClientTickEvents.END_WORLD_TICK.register(level -> EventDispatcher.post(new TickEvent.LevelEvent.Post(level)));

        InvalidateRenderStateCallback.EVENT.register(() -> ReloadRendersEvent.EVENT.invoker().reloadRenders());

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> EventDispatcher.post(new ClientNetworkEvent.LoggedIn(client.gameMode, client.player, handler.getConnection())));
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> EventDispatcher.post(new ClientNetworkEvent.LoggedOut(client.gameMode, client.player, handler.getConnection())));

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> CommandRegistryEvent.EVENT.invoker().registerCommands(dispatcher, dedicated ? Commands.CommandSelection.DEDICATED : Platform.getRunningServer().isPresent() ? Commands.CommandSelection.INTEGRATED : Commands.CommandSelection.ALL));
    }
}
