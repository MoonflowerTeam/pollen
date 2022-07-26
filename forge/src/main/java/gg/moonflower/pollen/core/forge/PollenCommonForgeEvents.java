package gg.moonflower.pollen.core.forge;

import gg.moonflower.pollen.api.event.events.LootTableConstructingEvent;
import gg.moonflower.pollen.api.event.events.entity.EntityEvents;
import gg.moonflower.pollen.api.event.events.entity.ModifyTradesEvents;
import gg.moonflower.pollen.api.event.events.entity.SetTargetEvent;
import gg.moonflower.pollen.api.event.events.entity.player.ContainerEvents;
import gg.moonflower.pollen.api.event.events.entity.player.PlayerInteractionEvents;
import gg.moonflower.pollen.api.event.events.entity.player.server.ServerPlayerTrackingEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.ServerLifecycleEvents;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvents;
import gg.moonflower.pollen.api.event.events.registry.CommandRegistryEvent;
import gg.moonflower.pollen.api.event.events.world.ChunkEvents;
import gg.moonflower.pollen.api.event.events.world.ExplosionEvents;
import gg.moonflower.pollen.core.Pollen;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = Pollen.MOD_ID)
public class PollenCommonForgeEvents {

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.TickEvent.ServerTickEvent event) {
        switch (event.phase) {
            case START:
                TickEvents.SERVER_PRE.invoker().tick();
                break;
            case END:
                TickEvents.SERVER_POST.invoker().tick();
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(TickEvent.LevelTickEvent event) {
        switch (event.phase) {
            case START:
                TickEvents.LEVEL_PRE.invoker().tick(event.level);
                break;
            case END:
                TickEvents.LEVEL_POST.invoker().tick(event.level);
                break;
        }
    }

    @SubscribeEvent
    public static void onEvent(LivingEvent.LivingTickEvent event) {
        if (!TickEvents.LIVING_PRE.invoker().tick(event.getEntity()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(ServerAboutToStartEvent event) {
        if (!ServerLifecycleEvents.PRE_STARTING.invoker().preStarting(event.getServer()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(ServerStartingEvent event) {
        if (!ServerLifecycleEvents.STARTING.invoker().starting(event.getServer()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(ServerStartedEvent event) {
        ServerLifecycleEvents.STARTED.invoker().started(event.getServer());
    }

    @SubscribeEvent
    public static void onEvent(ServerStoppingEvent event) {
        ServerLifecycleEvents.STOPPING.invoker().stopping(event.getServer());
    }

    @SubscribeEvent
    public static void onEvent(ServerStoppedEvent event) {
        ServerLifecycleEvents.STOPPED.invoker().stopped(event.getServer());
    }

    @SubscribeEvent
    public static void onEvent(RegisterCommandsEvent event) {
        CommandRegistryEvent.EVENT.invoker().registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem event) {
        InteractionResultHolder<ItemStack> result = PlayerInteractionEvents.RIGHT_CLICK_ITEM.invoker().interaction(event.getEntity(), event.getLevel(), event.getHand());
        if (result.getResult() != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result.getResult());
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock event) {
        InteractionResult result = PlayerInteractionEvents.RIGHT_CLICK_BLOCK.invoker().interaction(event.getEntity(), event.getLevel(), event.getHand(), event.getHitVec());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
        InteractionResult result = PlayerInteractionEvents.LEFT_CLICK_BLOCK.invoker().interaction(event.getEntity(), event.getLevel(), event.getHand(), event.getPos(), event.getFace());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract event) {
        InteractionResult result = PlayerInteractionEvents.RIGHT_CLICK_ENTITY.invoker().interaction(event.getEntity(), event.getLevel(), event.getHand(), event.getEntity());
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.level.ChunkEvent.Load event) {
        ChunkEvents.LOAD.invoker().load(event.getLevel(), event.getChunk());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.level.ChunkEvent.Unload event) {
        ChunkEvents.UNLOAD.invoker().unload(event.getLevel(), event.getChunk());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event) {
        ServerPlayerTrackingEvents.START_TRACKING_ENTITY.invoker().startTracking(event.getEntity(), event.getEntity());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.player.PlayerEvent.StopTracking event) {
        ServerPlayerTrackingEvents.STOP_TRACKING_ENTITY.invoker().stopTracking(event.getEntity(), event.getEntity());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent event) {
        SetTargetEvent.EVENT.invoker().setTarget(event.getEntity(), event.getTarget());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.level.ExplosionEvent.Start event) {
        if (!ExplosionEvents.START.invoker().start(event.getLevel(), event.getExplosion()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.level.ExplosionEvent.Detonate event) {
        ExplosionEvents.DETONATE.invoker().detonate(event.getLevel(), event.getExplosion(), event.getAffectedEntities());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.EntityJoinLevelEvent event) {
        if (!EntityEvents.JOIN.invoker().onJoin(event.getEntity(), event.getLevel()))
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.entity.EntityLeaveLevelEvent event) {
        EntityEvents.LEAVE.invoker().onLeave(event.getEntity(), event.getLevel());
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.village.VillagerTradesEvent event) {
        Int2ObjectMap<ModifyTradesEvents.TradeRegistry> newTrades = new Int2ObjectOpenHashMap<>();
        int minTier = event.getTrades().keySet().intStream().min().orElse(1);
        int maxTier = event.getTrades().keySet().intStream().max().orElse(5);
        ModifyTradesEvents.VILLAGER.invoker().modifyTrades(new ModifyTradesEvents.ModifyVillager.Context() {
            @Override
            public VillagerProfession getProfession() {
                return event.getType();
            }

            @Override
            public ModifyTradesEvents.TradeRegistry getTrades(int tier) {
                Validate.inclusiveBetween(minTier, maxTier, tier, "Tier must be between " + minTier + " and " + maxTier);
                return newTrades.computeIfAbsent(tier, key -> new ModifyTradesEvents.TradeRegistry());
            }

            @Override
            public int getMinTier() {
                return minTier;
            }

            @Override
            public int getMaxTier() {
                return maxTier;
            }
        });

        newTrades.forEach((tier, registry) -> event.getTrades().get(tier.intValue()).addAll(registry));
    }

    @SubscribeEvent
    public static void onEvent(net.minecraftforge.event.village.WandererTradesEvent event) {
        ModifyTradesEvents.TradeRegistry generic = new ModifyTradesEvents.TradeRegistry();
        ModifyTradesEvents.TradeRegistry rare = new ModifyTradesEvents.TradeRegistry();

        ModifyTradesEvents.WANDERER.invoker().modifyTrades(new ModifyTradesEvents.ModifyWanderer.Context() {
            @Override
            public ModifyTradesEvents.TradeRegistry getGeneric() {
                return generic;
            }

            @Override
            public ModifyTradesEvents.TradeRegistry getRare() {
                return rare;
            }
        });

        event.getGenericTrades().addAll(generic);
        event.getRareTrades().addAll(rare);
    }

    @SubscribeEvent
    public static void onEvent(PlayerContainerEvent.Open event) {
        ContainerEvents.OPEN.invoker().open(event.getEntity(), event.getContainer());
    }

    @SubscribeEvent
    public static void onEvent(PlayerContainerEvent.Close event) {
        ContainerEvents.CLOSE.invoker().close(event.getEntity(), event.getContainer());
    }

    @SubscribeEvent
    public static void onEvent(LootTableLoadEvent event) {
        LootTableConstructingEvent.Context context = new LootTableConstructingEvent.Context(event.getName(), event.getTable());
        LootTableConstructingEvent.EVENT.invoker().modifyLootTable(context);
        event.setTable(context.apply());
    }
}
