package gg.moonflower.pollen.core.client.profile;

import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.util.HttpUtil;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Ocelot
 */
public final class ProfileManager {

    private static final String HOST = "accounts.moonflower.gg";
    public static final ProfileConnection CONNECTION = new ProfileConnection("https://" + HOST + "/api/v1", "https://" + HOST + "/link");

    private static final Map<UUID, CompletableFuture<ProfileData>> PROFILES = new HashMap<>();

    private ProfileManager() {
    }

    public static synchronized void clearCache(UUID id) {
        PROFILES.remove(id);
    }

    public static synchronized CompletableFuture<ProfileData> getProfile(@Nullable UUID id) {
        return Pollen.CLIENT_CONFIG.disableMoonflowerProfiles.get() || id == null ? CompletableFuture.completedFuture(ProfileData.EMPTY) : PROFILES.computeIfAbsent(id, __ -> CompletableFuture.supplyAsync(() -> {
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
