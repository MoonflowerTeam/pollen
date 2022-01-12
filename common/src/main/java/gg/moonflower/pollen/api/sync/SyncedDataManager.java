package gg.moonflower.pollen.api.sync;

import com.mojang.serialization.Codec;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.event.events.lifecycle.TickEvents;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvents;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.core.network.login.ClientboundSyncPlayerDataKeysPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Manages data on players and syncs its data to clients based on specified properties.
 * <p>System based on <a href=https://github.com/MrCrayfish/Obfuscate/blob/1.16.X/src/main/java/com/mrcrayfish/obfuscate/common/data/SyncedPlayerData.java>Obfuscate</a> but modified to work cleaner on multiple platforms.
 *
 * @author Ocelot
 * @since 1.0.0
 **/
public final class SyncedDataManager {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, SyncedDataKey<?>> REGISTERED_KEYS = new HashMap<>();
    private static final Map<Integer, SyncedDataKey<?>> KEY_LOOKUP = new Int2ObjectArrayMap<>();
    private static final Map<Integer, SyncedDataKey<?>> CLIENT_KEY_LOOKUP = new Int2ObjectArrayMap<>();
    private static int nextId;
    private static boolean dirty;

    private SyncedDataManager() {
    }

    private static Map<Integer, SyncedDataKey<?>> getKeyLookup() {
        return !CLIENT_KEY_LOOKUP.isEmpty() ? CLIENT_KEY_LOOKUP : KEY_LOOKUP;
    }

    @ApiStatus.Internal
    public static void init() {
        TickEvents.SERVER_POST.register(() -> SyncedDataManager.dirty = false);
        TickEvents.LIVING_POST.register(entity -> {
            if (dirty)
                sync(entity);
        });
    }

    @ApiStatus.Internal
    public static void initClient() {
        ClientNetworkEvents.LOGOUT.register((controller, player, connection) -> CLIENT_KEY_LOOKUP.clear());
    }

    @ApiStatus.Internal
    @ExpectPlatform
    public static void sync(Entity entity) {
        Platform.error();
    }

    @ApiStatus.Internal
    public static void syncKeys(ClientboundSyncPlayerDataKeysPacket pkt) {
        CLIENT_KEY_LOOKUP.clear();
        pkt.getMappings().forEach((name, id) -> {
            SyncedDataKey<?> key = byName(name);
            if (key == null) {
                LOGGER.error("Server sent mapping for unknown key: " + name);
                return;
            }
            CLIENT_KEY_LOOKUP.put(id, key);
        });
    }

    @ApiStatus.Internal
    public static void markDirty() {
        dirty = true;
    }

    /**
     * Registers a new {@link SyncedDataKey}, created through {@link SyncedDataKey#builder(ResourceLocation, Codec, Supplier)}.
     *
     * @param key The key to register
     */
    public static synchronized void register(SyncedDataKey<?> key) {
        if (REGISTERED_KEYS.put(key.getKey(), key) != null)
            throw new IllegalStateException("Duplicate data key: " + key.getKey());
        KEY_LOOKUP.put(nextId++, key);
    }

    /**
     * Sets the value of the specified data key for the specified player.
     *
     * @param entity The entity to set the key for
     * @param key    The key to set
     * @param value  The new value
     * @param <T>    The type of data to set
     */
    @ExpectPlatform
    public static <T> void set(Entity entity, SyncedDataKey<T> key, T value) {
        Platform.error();
    }

    /**
     * Retrieves the value of the specified data key from the specified player.
     *
     * @param player The entity to get the data from
     * @param key    The key to get
     * @param <T>    The type of data to get
     */
    @ExpectPlatform
    public static <T> T get(Entity player, SyncedDataKey<T> key) {
        return Platform.error();
    }

    /**
     * Retrieves a synced data key by id.
     *
     * @param name The id of the key
     * @return The key with that id or <code>null</code> if there is no registered key
     */
    @Nullable
    public static SyncedDataKey<?> byName(ResourceLocation name) {
        return REGISTERED_KEYS.get(name);
    }

    /**
     * Retrieves the integer id of the specified key.
     *
     * @param key The key to check
     * @return The id of that key
     */
    public static int getId(SyncedDataKey<?> key) {
        return getKeyLookup().entrySet().stream().filter(entry -> entry.getValue() == key).mapToInt(Map.Entry::getKey).findFirst().orElseThrow(() -> new IllegalStateException("Attempted to get id of unregistered key: " + key.getKey()));
    }

    /**
     * Retrieves the key by the specified id.
     *
     * @param id The id of the key to retrieve
     * @return The key with that id
     */
    public static SyncedDataKey<?> byId(int id) {
        if (!getKeyLookup().containsKey(id))
            throw new IllegalStateException("Unknown synced data key with id: " + id);
        return getKeyLookup().get(id);
    }

    /**
     * @return All ids for all registered keys
     */
    public static Stream<Integer> getIds() {
        return getKeyLookup().keySet().stream();
    }
}
