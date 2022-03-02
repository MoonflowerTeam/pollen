package gg.moonflower.pollen.common.trades;

import gg.moonflower.pollen.api.event.events.entity.ModifyTradesEvents;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;

import java.util.Arrays;
import java.util.HashMap;
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
        ModifyTradesEvents.TradeRegistry generic = new ModifyTradesEvents.TradeRegistry();
        ModifyTradesEvents.TradeRegistry rare = new ModifyTradesEvents.TradeRegistry();
        generic.addAll(Arrays.asList(WANDERER_TRADES.get(1)));
        rare.addAll(Arrays.asList(WANDERER_TRADES.get(2)));

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

        VillagerTrades.WANDERING_TRADER_TRADES.put(1, generic.toArray(new VillagerTrades.ItemListing[0]));
        VillagerTrades.WANDERING_TRADER_TRADES.put(2, rare.toArray(new VillagerTrades.ItemListing[0]));
    }

    private static void registerVillagerTrades() {
        for (VillagerProfession prof : Registry.VILLAGER_PROFESSION) {
            Int2ObjectMap<VillagerTrades.ItemListing[]> vanillaTrades = VANILLA_TRADES.get(prof);
            Int2ObjectMap<ModifyTradesEvents.TradeRegistry> newTrades = new Int2ObjectOpenHashMap<>();

            if (vanillaTrades != null) {
                // Create trades for each
                for (Map.Entry<Integer, VillagerTrades.ItemListing[]> entry : vanillaTrades.int2ObjectEntrySet()) {
                    ModifyTradesEvents.TradeRegistry registry = new ModifyTradesEvents.TradeRegistry();
                    registry.addAll(Arrays.asList(entry.getValue()));
                    newTrades.put(entry.getKey().intValue(), registry);
                }
            } else {
                // There are no default trades to fill
                vanillaTrades = new Int2ObjectOpenHashMap<>();
                for (int i = 1; i < 6; i++)
                    newTrades.put(i, new ModifyTradesEvents.TradeRegistry());
            }

            int minTier = vanillaTrades.keySet().intStream().min().orElse(1);
            int maxTier = vanillaTrades.keySet().intStream().max().orElse(5);

            ModifyTradesEvents.VILLAGER.invoker().modifyTrades(new ModifyTradesEvents.ModifyVillager.Context() {
                @Override
                public VillagerProfession getProfession() {
                    return prof;
                }

                @Override
                public ModifyTradesEvents.TradeRegistry getTrades(int tier) {
                    Validate.inclusiveBetween(minTier, maxTier, tier, "Tier must be between " + minTier + " and " + maxTier);
                    return newTrades.get(tier);
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
            Int2ObjectMap<VillagerTrades.ItemListing[]> modifiedTrades = new Int2ObjectOpenHashMap<>();
            newTrades.int2ObjectEntrySet().forEach(e -> modifiedTrades.put(e.getIntKey(), e.getValue().toArray(new VillagerTrades.ItemListing[0])));
            VillagerTrades.TRADES.put(prof, modifiedTrades);
        }
    }
}
