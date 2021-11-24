package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.events.CommandRegistryEvent;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerInteractEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvent;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvent;
import gg.moonflower.pollen.api.event.events.world.ChunkEvent;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID)
public class PollenCommonForgeEvents {

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.TickEvent.ServerTickEvent event) {
        switch (event.phase) {
            case START:
                TickEvent.SERVER_PRE.invoker().tick();
                break;
            case END:
                TickEvent.SERVER_POST.invoker().tick();
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.TickEvent.WorldTickEvent event) {
        switch (event.phase) {
            case START:
                TickEvent.LEVEL_PRE.invoker().tick(event.world);
                break;
            case END:
                TickEvent.LEVEL_POST.invoker().tick(event.world);
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(LivingEvent.LivingUpdateEvent event) {
        event.setCanceled(!TickEvent.LIVING_PRE.invoker().tick(event.getEntityLiving()));
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStartingEvent event) {
        ServerLifecycleEvent.STARTING.invoker().starting(event.getServer());
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStartedEvent event) {
        ServerLifecycleEvent.STARTED.invoker().started(event.getServer());
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStoppingEvent event) {
        ServerLifecycleEvent.STOPPING.invoker().stopping(event.getServer());
    }

    @SubscribeEvent
    public static void onEvent(FMLServerStoppedEvent event) {
        ServerLifecycleEvent.STOPPED.invoker().stopped(event.getServer());
    }

    @SubscribeEvent
    public static void onEvent(RegisterCommandsEvent event) {
        CommandRegistryEvent.EVENT.invoker().registerCommands(event.getDispatcher(), event.getEnvironment());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        InteractionResultHolder<ItemStack> result = PlayerInteractEvent.RIGHT_CLICK_ITEM.invoker().interaction(event.getPlayer(), event.getWorld(), event.getHand());
        if (result.getResult() != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result.getResult());
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = PlayerInteractEvent.RIGHT_CLICK_BLOCK.invoker().interaction(event.getPlayer(), event.getWorld(), event.getHand(), event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
        InteractionResult result = PlayerInteractEvent.LEFT_CLICK_BLOCK.invoker().interaction(event.getPlayer(), event.getWorld(), event.getHand(), event.getPos(), event.getFace());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = PlayerInteractEvent.RIGHT_CLICK_ENTITY.invoker().interaction(event.getPlayer(), event.getWorld(), event.getHand(), event.getEntity());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.world.ChunkEvent.Load event) {
        ChunkEvent.LOAD.invoker().load(event.getWorld(), event.getChunk());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.world.ChunkEvent.Unload event) {
        ChunkEvent.UNLOAD.invoker().unload(event.getWorld(), event.getChunk());
    }
}
