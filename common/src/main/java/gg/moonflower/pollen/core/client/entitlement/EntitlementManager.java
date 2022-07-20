package gg.moonflower.pollen.core.client.entitlement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.Pair;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import gg.moonflower.pollen.api.event.events.client.render.AddRenderLayersEvent;
import gg.moonflower.pollen.api.event.events.entity.EntityEvents;
import gg.moonflower.pollen.api.event.events.network.ClientNetworkEvents;
import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.registry.resource.ResourceRegistry;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.profile.ProfileManager;
import gg.moonflower.pollen.core.client.render.layer.PollenCosmeticLayer;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.network.play.ServerboundUpdateSettingsPacket;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Retrieves and caches entitlements for all players.
 *
 * @author Ocelot
 */
@ApiStatus.Internal
public final class EntitlementManager {

    private static final Map<String, Entitlement> ENTITLEMENTS = new HashMap<>();
    private static final Map<UUID, EntitlementData> PLAYER_ENTITLEMENTS = new HashMap<>();
    private static final Logger LOGGER = LogManager.getLogger();

    static {
        EntityEvents.LEAVE.register((entity, level) -> {
            if (level.isClientSide() && entity instanceof Player && Minecraft.getInstance().player != entity)
                PLAYER_ENTITLEMENTS.remove(entity.getUUID());
        });
        ClientNetworkEvents.LOGOUT.register((controller, player, connection) -> PLAYER_ENTITLEMENTS.keySet().removeIf(id -> player != null && !player.getUUID().equals(id))); // Clear other player entitlements
    }

    private EntitlementManager() {
    }

    private static EntitlementData getData(UUID id) {
        return PLAYER_ENTITLEMENTS.computeIfAbsent(id, EntitlementData::new);
    }

    public static void init() {
        AddRenderLayersEvent.EVENT.register(context -> {
            for (String skin : context.getSkins()) {
                PlayerRenderer renderer = context.getSkin(skin);
                if (renderer != null)
                    renderer.addLayer(new PollenCosmeticLayer<>(context.getSkin(skin)));
            }
        });
        ResourceRegistry.registerReloadListener(PackType.CLIENT_RESOURCES, new PollinatedPreparableReloadListener() {
            @Override
            public ResourceLocation getPollenId() {
                return new ResourceLocation(Pollen.MOD_ID, "entitlements");
            }

            @Override
            public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
                ProfileManager.getProfile(Minecraft.getInstance().getUser().getGameProfile().getId()); // Don't wait for the profile to download
                return stage.wait(null);
            }
        });
    }

    public static CompletableFuture<Void> reload(boolean force, PreparableReloadListener.PreparationBarrier stage) {
        return force ? ProfileManager.getProfile(Minecraft.getInstance().getUser().getGameProfile().getId()).thenCompose(__ -> stage.wait(null)) : stage.wait(null); // Preload player profile
    }

    /**
     * Clears the cache of player entitlements.
     */
    public static void clearCache() {
        PLAYER_ENTITLEMENTS.clear();
    }

    /**
     * Clears the cache of player entitlements.
     *
     * @param id The player to remove entitlements for
     */
    public static void clearCache(UUID id) {
        PLAYER_ENTITLEMENTS.remove(id);
    }

    /**
     * @return All entitlements for all players
     */
    public static Stream<Entitlement> getAllEntitlements() {
        return ENTITLEMENTS.values().stream();
    }

    /**
     * Retrieves all entitlements for the player with the specified id.
     *
     * @param id The id of the player to retrieve entitlements from
     * @return All entitlements for that player or an empty stream if they have not yet loaded
     */
    public static Stream<Entitlement> getEntitlements(UUID id) {
        Map<String, Entitlement> entitlementMap = getEntitlementsFuture(id).getNow(Collections.emptyMap());
        return entitlementMap.isEmpty() ? Stream.empty() : entitlementMap.values().stream();
    }

    /**
     * Retrieves all entitlements for the player with the specified id.
     *
     * @param id The id of the player to retrieve entitlements from
     * @return All entitlements for that player
     */
    public static CompletableFuture<Map<String, Entitlement>> getEntitlementsFuture(UUID id) {
        return getData(id).getFuture();
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
        if (Pollen.CLIENT_CONFIG.disableMoonflowerProfiles.get()) // Don't try to update settings
            return;
        getEntitlementsFuture(id).thenApplyAsync(map -> {
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

            if (!json.entrySet().isEmpty() && Minecraft.getInstance().getConnection() != null)
                PollenMessages.PLAY.sendToServer(new ServerboundUpdateSettingsPacket(entitlementId, json));

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
        private volatile CompletableFuture<Map<String, Entitlement>> future;
        private volatile long expireTime;

        private EntitlementData(UUID id) {
            this.id = id;
            this.expireTime = 0;
        }

        public synchronized CompletableFuture<Map<String, Entitlement>> getFuture() {
            if (Pollen.CLIENT_CONFIG.disableMoonflowerProfiles.get()) // Don't even request entitlements if disabled
                return CompletableFuture.completedFuture(Collections.emptyMap());
            if (this.future != null && (!this.future.isDone() || System.currentTimeMillis() < this.expireTime))
                return this.future;
            return this.future = CompletableFuture.supplyAsync(() -> {
                try {
                    return ProfileManager.CONNECTION.getEntitlementSettings(this.id);
                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }, HttpUtil.DOWNLOAD_EXECUTOR).thenCompose(map -> CompletableFuture.allOf(map.keySet().stream().filter(entitlementId -> !ENTITLEMENTS.containsKey(entitlementId)).map(entitlementId -> CompletableFuture.supplyAsync(() -> {
                try {
                    return ProfileManager.CONNECTION.getEntitlement(entitlementId);
                } catch (IOException e) {
                    LOGGER.warn("Failed to retrieve entitlement: " + entitlementId, e);
                    return null;
                }
            }, HttpUtil.DOWNLOAD_EXECUTOR).thenAcceptAsync(entitlement -> {
                if (entitlement != null) {
                    ENTITLEMENTS.put(entitlementId, entitlement);
                } else {
                    map.remove(entitlementId);
                }
            }, Minecraft.getInstance())).toArray(CompletableFuture[]::new)).thenApply(__ -> map)).thenApplyAsync(map -> {
                Map<String, Entitlement> entitlementMap = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    Entitlement entitlement = ENTITLEMENTS.get(entry.getKey()).copy();
                    entitlement.updateSettings(entry.getValue());
                    return entitlement;
                }));

                this.expireTime = System.currentTimeMillis() + CACHE_TIME;
                // Defer to the next loop
                Minecraft.getInstance().tell(() -> {
                    if (entitlementMap.values().stream().anyMatch(entitlement -> entitlement instanceof ModelEntitlement))
                        GeometryModelManager.reload(false);
                    if (entitlementMap.values().stream().anyMatch(entitlement -> entitlement instanceof TexturedEntitlement))
                        GeometryTextureManager.reload(false);
                });
                return entitlementMap;
            }, Minecraft.getInstance()).exceptionally(e -> {
                if (!(e instanceof ProfileNotFoundException || (e instanceof CompletionException && e.getCause() instanceof ProfileNotFoundException)))
                    LOGGER.error("Failed to retrieve entitlements for " + this.id, e);
                this.expireTime = System.currentTimeMillis() + CACHE_TIME;
                return new HashMap<>();
            });
        }
    }
}
