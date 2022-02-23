package gg.moonflower.pollen.api.resource.modifier.type.fabric;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.world.level.storage.loot.Deserializers;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class LootModifierImpl {

    public static GsonBuilder createLootTableSerializer() {
        return Deserializers.createLootTableSerializer();
    }

    @ApiStatus.Internal
    public static void initPool(JsonObject jsonObject) {
    }
}
