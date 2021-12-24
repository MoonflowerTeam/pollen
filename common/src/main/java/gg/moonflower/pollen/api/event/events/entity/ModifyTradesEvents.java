package gg.moonflower.pollen.api.event.events.entity;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.List;

/**
 * Modifies trades for a merchant type.
 *
 * @author Jackson
 * @since 1.0.0
 */
public final class ModifyTradesEvents {

    public static final PollinatedEvent<ModifyVillager> VILLAGER = EventRegistry.createLoop(ModifyVillager.class);
    public static final PollinatedEvent<ModifyWanderer> WANDERER = EventRegistry.createLoop(ModifyWanderer.class);

    private ModifyTradesEvents() {
    }

    public interface ModifyVillager {
        void modifyTrades(Int2ObjectMap<List<VillagerTrades.ItemListing>> trades, VillagerProfession type);
    }

    public interface ModifyWanderer {
        void modifyTrades(List<VillagerTrades.ItemListing> generic, List<net.minecraft.world.entity.npc.VillagerTrades.ItemListing> rare);
    }
}
