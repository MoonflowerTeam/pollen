package gg.moonflower.pollen.api.sync;

import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public abstract class DataComponent {

    private static final Logger LOGGER = LogManager.getLogger();

    protected final Map<SyncedDataKey<?>, Object> values;
    protected final Set<Integer> dirtyValues;

    protected DataComponent() {
        this.values = new HashMap<>();
        this.dirtyValues = ConcurrentHashMap.newKeySet();
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(SyncedDataKey<T> key) {
        return (T) this.values.computeIfAbsent(key, k -> k.getDefaultValueSupplier().get());
    }

    public <T> void setValue(SyncedDataKey<T> key, T value) {
        T oldValue = this.getValue(key);
        this.values.put(key, value);
        if (!Objects.equals(oldValue, value))
            this.dirtyValues.add(SyncedDataManager.getId(key));
    }

    public boolean shouldSyncWith(Entity provider, Entity entity) {
        return this.values.keySet().stream().map(key -> (provider == entity && key.getSyncStrategy().isSyncEntity()) || key.getSyncStrategy().isSyncTracking()).reduce(false, (a, b) -> a || b);
    }

    public boolean shouldCopyForRespawn(boolean lossless, boolean keepInventory) {
        return this.values.keySet().stream().map(SyncedDataKey::isPersistent).reduce(false, (a, b) -> a || b);
    }

    public void copyForRespawn(DataComponent original, boolean lossless) {
        CompoundTag tag = new CompoundTag();
        original.writeToNbt(tag, lossless ? NbtWriteMode.COPY : NbtWriteMode.RESPAWN);
        this.readFromNbt(tag);
    }

    public void clean() {
        this.dirtyValues.clear();
    }

    public boolean isDirty() {
        return !this.dirtyValues.isEmpty();
    }

    public void readFromNbt(CompoundTag nbt) {
        for (String key : nbt.getAllKeys()) {
            Tag tag = nbt.get(key);
            try {
                ResourceLocation name = new ResourceLocation(key);
                SyncedDataKey<?> syncedDataKey = SyncedDataManager.byName(name);
                if (syncedDataKey == null)
                    throw new IllegalStateException("Unknown synced data key: " + name);
                this.values.put(syncedDataKey, this.readWithCodec(syncedDataKey, tag));
            } catch (Exception e) {
                LOGGER.error("Failed to decode " + key + " from NBT: " + tag, e);
            }
        }
    }

    public void writeToNbt(CompoundTag nbt, NbtWriteMode mode) {
        for (Map.Entry<SyncedDataKey<?>, Object> entry : this.values.entrySet()) {
            SyncedDataKey<?> key = entry.getKey();
            if (mode == NbtWriteMode.SAVE && !key.isSave())
                continue;
            if (mode == NbtWriteMode.RESPAWN && !key.isPersistent())
                continue;
            this.writeWithCodec(key).ifPresent(tag -> nbt.put(key.getKey().toString(), tag));
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    protected void writePacketData(FriendlyByteBuf buf, int[] ids) {
        buf.writeVarInt(ids.length);
        for (int i : ids) {
            buf.writeVarInt(i);
            SyncedDataKey key = SyncedDataManager.byId(i);

            CompoundTag tag = new CompoundTag();
            this.writeWithCodec(key).ifPresent(data -> tag.put("a", (Tag) data));
            buf.writeNbt(tag);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void applySyncPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            SyncedDataKey key = SyncedDataManager.byId(buf.readVarInt());

            CompoundTag tag = buf.readNbt();
            if (tag != null && tag.contains("a"))
                this.values.put(key, this.readWithCodec(key, tag.get("a")));
            buf.writeNbt(tag);
        }
    }

    protected <T> T readWithCodec(SyncedDataKey<T> key, Tag data) {
        DataResult<T> dataResult = key.getCodec().parse(NbtOps.INSTANCE, data);
        if (dataResult.error().isPresent() || !dataResult.result().isPresent()) {
            LOGGER.error("Failed to decode " + key.getKey() + " from NBT: " + dataResult.error().get().message() + " " + data);
            return key.getDefaultValueSupplier().get();
        } else {
            return dataResult.result().get();
        }
    }

    protected <T> Optional<Tag> writeWithCodec(SyncedDataKey<T> key) {
        if (!this.values.containsKey(key))
            return Optional.empty();

        T value = this.getValue(key);
        DataResult<Tag> dataResult = key.getCodec().encodeStart(NbtOps.INSTANCE, value);
        if (dataResult.error().isPresent() || !dataResult.result().isPresent()) {
            LOGGER.error("Failed to encode " + key.getKey() + " to NBT: " + dataResult.error().get().message() + " " + value);
            return Optional.empty();
        } else {
            return dataResult.result();
        }
    }

    public enum NbtWriteMode {
        COPY, SAVE, RESPAWN
    }
}
