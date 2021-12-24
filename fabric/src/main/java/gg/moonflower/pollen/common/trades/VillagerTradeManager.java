package gg.moonflower.pollen.common.trades;

import gg.moonflower.pollen.api.event.events.entity.ModifyTradesEvents;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class VillagerTradeManager {

    private static final Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>> VANILLA_TRADES = new HashMap<>();
    private static final Int2ObjectMap<VillagerTrades.ItemListing[]> WANDERER_TRADES = new Int2ObjectOpenHashMap<>();

    static {
        VillagerTrades.TRADES.forEach((key, value) -> {
            Int2ObjectMap<VillagerTrades.ItemListing[]> copy = new Int2ObjectOpenHashMap<>();
            value.int2ObjectEntrySet().forEach(ent -> copy.put(ent.getIntKey(), Arrays.copyOf(ent.getValue(), ent.getValue().length)));
            VANILLA_TRADES.put(key, copy);
        });
        VillagerTrades.WANDERING_TRADER_TRADES.int2ObjectEntrySet().forEach(e -> WANDERER_TRADES.put(e.getIntKey(), Arrays.copyOf(e.getValue(), e.getValue().length)));
    }

    public static void init() {
        registerWandererTrades();
        registerVillagerTrades();
    }

    // From MinecraftForge to keep platforms consistent

    private static void registerWandererTrades() {
        List<VillagerTrades.ItemListing> generic = NonNullList.create();
        List<VillagerTrades.ItemListing> rare = NonNullList.create();
        generic.addAll(Arrays.asList(WANDERER_TRADES.get(1)));
        rare.addAll(Arrays.asList(WANDERER_TRADES.get(2)));

        ModifyTradesEvents.WANDERER.invoker().modifyTrades(generic, rare);

        VillagerTrades.WANDERING_TRADER_TRADES.put(1, generic.toArray(new VillagerTrades.ItemListing[0]));
        VillagerTrades.WANDERING_TRADER_TRADES.put(2, rare.toArray(new VillagerTrades.ItemListing[0]));
    }

    private static void registerVillagerTrades() {
        for (VillagerProfession prof : Registry.VILLAGER_PROFESSION) {
            Int2ObjectMap<VillagerTrades.ItemListing[]> vanillaTrades = VANILLA_TRADES.getOrDefault(prof, new Int2ObjectOpenHashMap<>());
            Int2ObjectMap<List<VillagerTrades.ItemListing>> newTrades = new Int2ObjectOpenHashMap<>();
            for (int i = 1; i < 6; i++) {
                newTrades.put(i, NonNullList.create());
            }

            vanillaTrades.int2ObjectEntrySet().forEach(e -> Arrays.stream(e.getValue()).forEach(newTrades.get(e.getIntKey())::add));
            ModifyTradesEvents.VILLAGER.invoker().modifyTrades(newTrades, prof);
            Int2ObjectMap<VillagerTrades.ItemListing[]> modifiedTrades = new Int2ObjectOpenHashMap<>();
            newTrades.int2ObjectEntrySet().forEach(e -> modifiedTrades.put(e.getIntKey(), e.getValue().toArray(new VillagerTrades.ItemListing[0])));
            VillagerTrades.TRADES.put(prof, modifiedTrades);
        }
    }

}
