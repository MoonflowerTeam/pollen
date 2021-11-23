package gg.moonflower.pollen.core.fabric;

import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.fabric.ConfigTracker;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvent;
import gg.moonflower.pollen.api.event.events.player.InteractEvent;
import gg.moonflower.pollen.core.Pollen;
import jdk.internal.ref.Cleaner;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.InteractionResultHolder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        Pollen.init();

        ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.COMMON, FabricLoader.getInstance().getConfigDir());
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT)
            ConfigTracker.INSTANCE.loadConfigs(PollinatedConfigType.CLIENT, FabricLoader.getInstance().getConfigDir());

        Pollen.PLATFORM.setup();

        ServerTickEvents.START_SERVER_TICK.register(level -> EventDispatcher.post(new TickEvent.ServerEvent.Pre()));
        ServerTickEvents.END_SERVER_TICK.register(level -> EventDispatcher.post(new TickEvent.ServerEvent.Post()));
        ServerTickEvents.START_WORLD_TICK.register(level -> EventDispatcher.post(new TickEvent.LevelEvent.Pre(level)));
        ServerTickEvents.END_WORLD_TICK.register(level -> EventDispatcher.post(new TickEvent.LevelEvent.Post(level)));

        ServerLifecycleEvents.SERVER_STARTING.register(server -> EventDispatcher.post(new ServerLifecycleEvent.Starting(server)));
        ServerLifecycleEvents.SERVER_STARTED.register(server -> EventDispatcher.post(new ServerLifecycleEvent.Started(server)));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> EventDispatcher.post(new ServerLifecycleEvent.Stopping(server)));
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> EventDispatcher.post(new ServerLifecycleEvent.Stopped(server)));

        UseItemCallback.EVENT.register((player, world, hand) -> {
            InteractEvent.UseItem event = new InteractEvent.UseItem(player, world, hand);
            EventDispatcher.post(event);
            return new InteractionResultHolder<>(event.getResult(), event.getPlayer().getItemInHand(event.getHand()));
        });

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            InteractEvent.UseBlock event = new InteractEvent.UseBlock(player, world, hand, hitResult);
            EventDispatcher.post(event);
            return event.getResult();
        });
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            InteractEvent.AttackBlock event = new InteractEvent.AttackBlock(player, world, hand, pos, direction);
            EventDispatcher.post(event);
            return event.getResult();
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, entityHitResult) -> {
            InteractEvent.UseEntity event = new InteractEvent.UseEntity(player, world, hand, entity);
            EventDispatcher.post(event);
            return event.getResult();
        });

        EventDispatcher.register(FabricEvents.class);
    }
}
