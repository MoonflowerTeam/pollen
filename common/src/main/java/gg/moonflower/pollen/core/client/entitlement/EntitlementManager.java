package gg.moonflower.pollen.core.client.entitlement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.Pair;
import gg.moonflower.pollen.api.event.events.entity.player.server.ServerPlayerTrackingEvents;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvents;
import gg.moonflower.pollen.core.client.profile.ProfileManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Retrieves and caches entitlements for all players.
 *
 * @author Ocelot
 */
@ApiStatus.Internal
public final class EntitlementManager {

    private static final Map<UUID, EntitlementData> ENTITLEMENTS = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        ServerPlayerTrackingEvents.STOP_TRACKING_ENTITY.register((player, entity) -> {
            if (entity instanceof Player)
                ENTITLEMENTS.remove(entity.getUUID());
        });
        ClientNetworkEvents.LOGOUT.register((controller, player, connection) -> ENTITLEMENTS.clear());
    }

    private EntitlementManager() {
    }

    private static EntitlementData getData(UUID id) {
        return ENTITLEMENTS.computeIfAbsent(id, EntitlementData::new);
    }

    /**
     * @return All entitlements for all players
     */
    public static Stream<Entitlement> getAllEntitlements() {
        return ENTITLEMENTS.keySet().stream().flatMap(EntitlementManager::getEntitlements);
    }

    /**
     * Retrieves all entitlements for the player with the specified id.
     *
     * @param id The id of the player to retrieve entitlements from
     * @return All entitlements for that player or an empty stream if they have not yet loaded
     */
    public static Stream<Entitlement> getEntitlements(UUID id) {
        Map<String, Entitlement> entitlementMap = getData(id).getFuture().getNow(Collections.emptyMap());
        return entitlementMap.isEmpty() ? Stream.empty() : entitlementMap.values().stream();
    }

    /**
     * Updates settings for the specified entitlement.
     *
     * @param id            The id of the player to change data for
     * @param entitlementId The id of the entitlement to fetch
     * @param action        The action to take on the entitlement to update settings
     * @param <T>           The type of entitlement to update
     */
    @SuppressWarnings("unchecked")
    public static <T extends Entitlement> void updateEntitlementSettings(UUID id, String entitlementId, Consumer<T> action) {
        getData(id).getFuture().thenApplyAsync(map -> {
            if (!map.containsKey(entitlementId))
                return null;

            T entitlement = (T) map.get(entitlementId);
            JsonObject oldSettings = entitlement.saveSettings();
            action.accept(entitlement);

            JsonObject json = entitlement.saveSettings();
            Set<String> unchangedElements = new HashSet<>(json.size() / 2); // By default, more than half of the elements have probably not changed
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (oldSettings.has(entry.getKey()) && entry.getValue().equals(oldSettings.get(entry.getKey()))) {
                    unchangedElements.add(entry.getKey());
                }
            }
            unchangedElements.forEach(json::remove);

            return Pair.of(entitlement, json);
        }, Minecraft.getInstance()).thenAcceptAsync(pair -> {
            if (pair == null || pair.second.entrySet().isEmpty()) // If nothing has changed don't send a request
                return;
            try {
                pair.first.updateSettings(ProfileManager.CONNECTION.updateSettings(id, entitlementId, pair.second));
            } catch (IOException e) {
                throw new CompletionException("Failed to update entitlement settings for " + entitlementId, e);
            }
        }, HttpUtil.DOWNLOAD_EXECUTOR);
    }

    private static class EntitlementData {

        private static final long CACHE_TIME = TimeUnit.HOURS.toMillis(1);

        private final UUID id;
        private CompletableFuture<Map<String, Entitlement>> future;
        private long expireTime;

        private EntitlementData(UUID id) {
            this.id = id;
            this.expireTime = 0;
        }

        public synchronized CompletableFuture<Map<String, Entitlement>> getFuture() {
            if (this.future != null && (!this.future.isDone() || System.currentTimeMillis() < this.expireTime))
                return this.future;
            return this.future = CompletableFuture.supplyAsync(() -> {
                try {
                    return ProfileManager.CONNECTION.getEntitlements(this.id);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, HttpUtil.DOWNLOAD_EXECUTOR).thenCompose(map -> CompletableFuture.allOf(map.entrySet().stream().map(entry -> CompletableFuture.runAsync(() -> {
                try {
                    entry.getValue().updateSettings(ProfileManager.CONNECTION.getSettings(this.id, entry.getKey()));
                } catch (IOException e) {
                    LOGGER.warn("Failed to retrieve entitlement settings for " + entry.getKey(), e);
                }
            }, HttpUtil.DOWNLOAD_EXECUTOR)).toArray(CompletableFuture[]::new)).thenApply(__ -> map)).thenApplyAsync(map -> {
                this.expireTime = System.currentTimeMillis() + CACHE_TIME;
                Minecraft.getInstance().execute(() -> {
                    if (map.values().stream().anyMatch(entitlement -> entitlement instanceof ModelEntitlement))
                        GeometryModelManager.reload(false);
                    if (map.values().stream().anyMatch(entitlement -> entitlement instanceof TexturedEntitlement))
                        GeometryTextureManager.reload(false);
                });
                return map;
            }, Minecraft.getInstance()).exceptionally(e -> {
                LOGGER.error("Failed to retrieve entitlements for " + this.id, e);
                this.expireTime = System.currentTimeMillis() + CACHE_TIME;
                return new HashMap<>();
            });
        }
    }
}
