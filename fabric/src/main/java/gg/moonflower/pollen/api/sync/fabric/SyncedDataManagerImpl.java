package gg.moonflower.pollen.api.sync.fabric;

import gg.moonflower.pollen.api.sync.SyncedDataKey;
import gg.moonflower.pollen.api.sync.SyncedDataManager;
import gg.moonflower.pollen.core.fabric.PollenComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SyncedDataManagerImpl {

    public static void sync(ServerPlayer player) {
        PollenComponents.SYNCED_DATA.sync(player);
    }

    public static <T> void set(Player player, SyncedDataKey<T> key, T value) {
        PollenComponents.SYNCED_DATA.get(player).setValue(key, value);
        SyncedDataManager.markDirty();
    }

    public static <T> T get(Player player, SyncedDataKey<T> key) {
        return PollenComponents.SYNCED_DATA.get(player).getValue(key);
    }
}
