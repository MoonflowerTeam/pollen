package gg.moonflower.pollen.api.sync.fabric;

import gg.moonflower.pollen.api.sync.SyncedDataKey;
import gg.moonflower.pollen.api.sync.SyncedDataManager;
import gg.moonflower.pollen.core.fabric.PollenComponents;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class SyncedDataManagerImpl {

    public static void sync(Entity entity) {
        PollenComponents.SYNCED_DATA.sync(entity);
    }

    public static <T> void set(Entity entity, SyncedDataKey<T> key, T value) {
        PollenComponents.SYNCED_DATA.get(entity).setValue(key, value);
        SyncedDataManager.markDirty();
    }

    public static <T> T get(Entity entity, SyncedDataKey<T> key) {
        return PollenComponents.SYNCED_DATA.get(entity).getValue(key);
    }
}
