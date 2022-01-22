package gg.moonflower.pollen.core.client.profile;

import net.minecraft.util.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author Ocelot
 */
public final class ProfileManager {

    public static final ProfileConnection CONNECTION = new ProfileConnection("http://localhost:8080/v1");

    private static final Map<UUID, CompletableFuture<ProfileData>> PROFILES = new HashMap<>();

    private ProfileManager() {
    }

    public static CompletableFuture<ProfileData> getProfile(UUID id) {
        return PROFILES.computeIfAbsent(id, __ -> CompletableFuture.supplyAsync(() -> {
            try {
                return CONNECTION.getProfileData(id);
            } catch (IOException e) {
                throw new CompletionException("Failed to retrieve profile data", e);
            }
        }, HttpUtil.DOWNLOAD_EXECUTOR));
    }
}
