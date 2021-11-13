package gg.moonflower.pollen.api.sync;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface DataComponent {

    <T> boolean hasValue(SyncedDataKey<T> key);

    <T> T getValue(SyncedDataKey<T> key);

    <T> void setValue(SyncedDataKey<T> key, T value);

    void readFromNbt(CompoundTag tag);

    void writeToNbt(CompoundTag tag, NbtWriteMode mode);

    enum NbtWriteMode {
        COPY, SAVE, RESPAWN
    }
}
