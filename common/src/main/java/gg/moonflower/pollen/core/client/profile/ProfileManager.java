package gg.moonflower.pollen.core.client.profile;

import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import net.minecraft.util.HttpUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Ocelot
 */
public final class ProfileManager {

    public static final ProfileConnection CONNECTION = new ProfileConnection("https://accounts.moonflower.gg/api/v1", "https://accounts.moonflower.gg/link");

    private static final Map<UUID, CompletableFuture<ProfileData>> PROFILES = new HashMap<>();

    private ProfileManager() {
    }

    public static synchronized void clearCache(UUID id) {
        PROFILES.remove(id);
    }

    public static synchronized CompletableFuture<ProfileData> getProfile(UUID id) {
        return PROFILES.computeIfAbsent(id, __ -> CompletableFuture.supplyAsync(() -> {
            try {
                return CONNECTION.getProfileData(id);
            } catch (ProfileNotFoundException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ProfileData.EMPTY;
        }, HttpUtil.DOWNLOAD_EXECUTOR));
    }
}
