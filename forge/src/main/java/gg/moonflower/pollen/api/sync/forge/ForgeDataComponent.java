package gg.moonflower.pollen.api.sync.forge;

import com.mojang.serialization.DataResult;
import gg.moonflower.pollen.api.sync.DataComponent;
import gg.moonflower.pollen.api.sync.SyncedDataKey;
import gg.moonflower.pollen.api.sync.SyncedDataManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class ForgeDataComponent implements DataComponent {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<SyncedDataKey<?>, Object> values;
    private final Set<Integer> dirtyValues;

    public ForgeDataComponent() {
        this.values = new HashMap<>();
        this.dirtyValues = ConcurrentHashMap.newKeySet();
    }

    @Override
    public <T> boolean hasValue(SyncedDataKey<T> key) {
        return this.values.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(SyncedDataKey<T> key) {
        return (T) this.values.computeIfAbsent(key, k -> k.getDefaultValueSupplier().get());
    }

    @Override
    public <T> void setValue(SyncedDataKey<T> key, T value) {
        T oldValue = this.getValue(key);
        this.values.put(key, value);
        if (!Objects.equals(oldValue, value))
            this.dirtyValues.add(SyncedDataManager.getId(key));
    }

    public boolean shouldSyncWith(ServerPlayer provider, ServerPlayer player) {
        return this.values.keySet().stream().map(key -> (provider == player && key.getSyncStrategy().isSyncEntity()) || key.getSyncStrategy().isSyncTracking()).reduce(false, (a, b) -> a || b);
    }

    public boolean shouldCopyForRespawn(boolean lossless, boolean keepInventory, boolean sameCharacter) {
        return this.values.keySet().stream().map(SyncedDataKey::isPersistent).reduce(false, (a, b) -> a || b);
    }

    public void copyForRespawn(ForgeDataComponent original, boolean lossless) {
        CompoundTag tag = new CompoundTag();
        original.writeToNbt(tag, lossless ? NbtWriteMode.COPY : NbtWriteMode.RESPAWN);
        this.readFromNbt(tag);
    }

    public void copyFrom(ForgeDataComponent other) {
        CompoundTag tag = new CompoundTag();
        other.writeToNbt(tag, NbtWriteMode.COPY);
        this.readFromNbt(tag);
    }

    public void clean() {
        this.dirtyValues.clear();
    }

    public boolean isDirty() {
        return !this.dirtyValues.isEmpty();
    }

    @Override
    public void readFromNbt(CompoundTag nbt) {
        for (String key : nbt.getAllKeys()) {
            Tag tag = nbt.get(key);
            try {
                ResourceLocation name = new ResourceLocation(key);
                SyncedDataKey<?> syncedDataKey = SyncedDataManager.byName(name);
                this.values.put(syncedDataKey, this.readWithCodec(syncedDataKey, tag));
            } catch (Exception e) {
                LOGGER.error("Failed to decode " + key + " from NBT: " + tag, e);
            }
        }
    }

    @Override
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

    public void writeSyncPacket(FriendlyByteBuf buf, ServerPlayer provider, ServerPlayer player) {
        int[] ids = this.values.keySet().stream().filter(key -> (provider == player && key.getSyncStrategy().isSyncEntity()) || key.getSyncStrategy().isSyncTracking()).mapToInt(SyncedDataManager::getId).toArray();
        writePacketData(buf, ids);
    }

    public void writeUpdatePacket(FriendlyByteBuf buf, ServerPlayer provider, ServerPlayer player) {
        int[] ids = this.dirtyValues.stream().map(SyncedDataManager::byId).filter(key -> (provider == player && key.getSyncStrategy().isSyncEntity()) || key.getSyncStrategy().isSyncTracking()).mapToInt(SyncedDataManager::getId).toArray();
        writePacketData(buf, ids);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void writePacketData(FriendlyByteBuf buf, int[] ids) {
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
    public void applyUpdatePacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        for (int i = 0; i < size; i++) {
            SyncedDataKey key = SyncedDataManager.byId(buf.readVarInt());

            CompoundTag tag = buf.readNbt();
            if (tag != null && tag.contains("a"))
                this.values.put(key, this.readWithCodec(key, tag.get("a")));
            buf.writeNbt(tag);
        }
    }

    private <T> T readWithCodec(SyncedDataKey<T> key, Tag data) {
        DataResult<T> dataResult = key.getCodec().parse(NbtOps.INSTANCE, data);
        if (dataResult.error().isPresent() || !dataResult.result().isPresent()) {
            LOGGER.error("Failed to decode " + key.getKey() + " from NBT: " + dataResult.error().get().message() + " " + data);
            return key.getDefaultValueSupplier().get();
        } else {
            return dataResult.result().get();
        }
    }

    private <T> Optional<Tag> writeWithCodec(SyncedDataKey<T> key) {
        if (!this.hasValue(key))
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
}
