package gg.moonflower.pollen.api.sync;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class SyncedDataKey<T> {

    private final ResourceLocation key;
    private final Codec<T> codec;
    private final Supplier<T> defaultValueSupplier;
    private final boolean save;
    private final boolean persistent;
    private final SyncStrategy syncStrategy;

    private SyncedDataKey(ResourceLocation key, Codec<T> codec, Supplier<T> defaultValueSupplier, boolean save, boolean persistent, SyncStrategy syncStrategy) {
        this.key = key;
        this.codec = codec;
        this.defaultValueSupplier = defaultValueSupplier;
        this.save = save;
        this.persistent = persistent;
        this.syncStrategy = syncStrategy;
    }

    public static <T> SyncedDataKey.Builder<T> builder(ResourceLocation id, Codec<T> codec, Supplier<T> defaultValueSupplier) {
        return new Builder<>(id, codec, defaultValueSupplier);
    }

    public ResourceLocation getKey() {
        return key;
    }

    public Codec<T> getCodec() {
        return codec;
    }

    public Supplier<T> getDefaultValueSupplier() {
        return defaultValueSupplier;
    }

    public boolean isSave() {
        return save;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public SyncStrategy getSyncStrategy() {
        return syncStrategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyncedDataKey<?> that = (SyncedDataKey<?>) o;
        return this.key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }

    @Override
    public String toString() {
        return "SyncedDataKey{" +
                "key=" + key +
                ", save=" + save +
                ", persistent=" + persistent +
                ", syncStrategy=" + syncStrategy +
                '}';
    }

    public enum SyncStrategy {

        /**
         * Stops the synced key from syncing at all. The data will only be present on the server.
         */
        NONE(false, false),
        /**
         * Only syncs the data to the client with the data. The data will only be present on the server and for the client with the data.
         */
        ENTITY(true, false),
        /**
         * Only syncs the data to the clients tracking the client with the data. The data will only be present on the server and for the clients tracking the client with the data.
         */
        TRACKING(false, true),
        /**
         * Fully syncs the data. The data will be present on the server, the client with the data, and all players tracking the client with the data.
         */
        TRACKING_AND_SELF(true, true);

        private final boolean syncEntity;
        private final boolean syncTracking;

        SyncStrategy(boolean syncEntity, boolean syncTracking) {
            this.syncEntity = syncEntity;
            this.syncTracking = syncTracking;
        }

        public boolean isSyncEntity() {
            return syncEntity;
        }

        public boolean isSyncTracking() {
            return syncTracking;
        }
    }

    public static class Builder<T> {

        private final ResourceLocation id;
        private final Codec<T> codec;
        private final Supplier<T> defaultValueSupplier;
        private boolean save = false;
        private boolean persistent = true;
        private SyncStrategy syncStrategy = SyncStrategy.TRACKING_AND_SELF;

        private Builder(ResourceLocation id, Codec<T> codec, Supplier<T> defaultValueSupplier) {
            this.id = id;
            this.codec = codec;
            this.defaultValueSupplier = defaultValueSupplier;
        }

        /**
         * @return A new {@link SyncedDataKey} with all defined properties
         */
        public SyncedDataKey<T> build() {
            return new SyncedDataKey<>(this.id, this.codec, this.defaultValueSupplier, this.save, this.persistent, this.syncStrategy);
        }

        /**
         * Saves this synced key to disc so the data will persist after leaving and re-entering the world.
         */
        public Builder<T> saveToFile() {
            this.save = true;
            return this;
        }

        /**
         * Resets the data when the player dies.
         */
        public Builder<T> resetOnDeath() {
            this.persistent = false;
            return this;
        }

        /**
         * Sets the strategy for data sync between the client and server.
         *
         * @param syncStrategy The strategy to use
         */
        public Builder<T> syncStrategy(SyncStrategy syncStrategy) {
            this.syncStrategy = syncStrategy;
            return this;
        }
    }
}
