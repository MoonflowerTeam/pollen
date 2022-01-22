package gg.moonflower.pollen.core.client.entitlement;

import com.google.gson.JsonObject;
import com.ibm.icu.impl.Pair;
import gg.moonflower.pollen.core.client.profile.ProfileManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.HttpUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

public final class EntitlementManager {

    private static final Map<UUID, EntitlementData> ENTITLEMENTS = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();

    private EntitlementManager() {
    }

    private static EntitlementData getData(UUID id) {
        return ENTITLEMENTS.computeIfAbsent(id, EntitlementData::new);
    }

    public static CompletableFuture<Map<String, Entitlement>> getEntitlements(UUID id) {
        return getData(id).getFuture();
    }

    public static void setCosmeticSettings(UUID id, String entitlement, boolean enabled) {
        getEntitlements(id).thenApplyAsync(map -> {
            if (!map.containsKey(entitlement) || !(map.get(entitlement) instanceof Cosmetic))
                return null;

            Cosmetic cosmetic = (Cosmetic) map.get(entitlement);
            boolean oldEnabled = cosmetic.isEnabled();
            cosmetic.setEnabled(enabled);

            JsonObject json = new JsonObject();
            if (oldEnabled != cosmetic.isEnabled())
                json.addProperty("enabled", cosmetic.isEnabled());

            return Pair.of(cosmetic, json);
        }, Minecraft.getInstance()).thenAcceptAsync(pair -> {
            if (pair == null)
                return;
            Cosmetic cosmetic = pair.first;
            JsonObject json = pair.second;

            try {
                cosmetic.updateSettings(ProfileManager.CONNECTION.updateSettings(id, entitlement, json));
            } catch (IOException e) {
                throw new CompletionException("Failed to update entitlement settings for " + entitlement, e);
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

        public CompletableFuture<Map<String, Entitlement>> getFuture() {
            if (this.future != null && System.currentTimeMillis() < this.expireTime)
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
                return map;
            }, Minecraft.getInstance()).exceptionally(e -> {
                LOGGER.error("Failed to retrieve entitlements for " + this.id, e);
                return new HashMap<>();
            });
        }
    }
}
