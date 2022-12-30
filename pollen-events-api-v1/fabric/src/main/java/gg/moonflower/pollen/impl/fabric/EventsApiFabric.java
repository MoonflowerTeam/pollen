package gg.moonflower.pollen.impl.fabric;

import gg.moonflower.pollen.api.events.v1.LootTableConstructingEvent;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;

public class EventsApiFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        LootTableEvents.REPLACE.register((resourceManager, manager, id, source, setter) -> {
            LootTableConstructingEvent.Context context = new LootTableConstructingEvent.Context(id, source);
            LootTableConstructingEvent.EVENT.invoker().modifyLootTable(context);
            return context.apply();
        });
    }
}
