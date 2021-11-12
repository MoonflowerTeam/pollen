package gg.moonflower.pollen.api.client.util;

import com.google.common.collect.Iterables;
import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.StringUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * Loads and caches game profiles from {@link SkullBlockEntity}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class SkinHelper {

    private static final Map<GameProfile, GameProfile> PROFILE_CACHE = new WeakHashMap<>();
    @Environment(EnvType.CLIENT)
    private static MinecraftSessionService sessionService;
    @Environment(EnvType.CLIENT)
    private static GameProfileCache gameProfileCache;

    @ApiStatus.Internal
    public static void init() {
        AuthenticationService authenticationservice = new YggdrasilAuthenticationService(Minecraft.getInstance().getProxy());
        sessionService = authenticationservice.createMinecraftSessionService();
        gameProfileCache = new GameProfileCache(authenticationservice.createProfileRepository(), new File(Minecraft.getInstance().gameDirectory, MinecraftServer.USERID_CACHE_FILE.getName()));
    }

    private static GameProfile guiUpdateGameProfile(GameProfile input) {
        if (input.isComplete() && input.getProperties().containsKey("textures"))
            return input;
        if (StringUtil.isNullOrEmpty(input.getName()) && input.getId() == null)
            return input;

        if (StringUtil.isNullOrEmpty(input.getName())) {
            GameProfile gameProfile2 = gameProfileCache.get(input.getId());
            if (gameProfile2 == null)
                return input;
            Property property = Iterables.getFirst(gameProfile2.getProperties().get("textures"), null);
            if (property == null)
                gameProfile2 = sessionService.fillProfileProperties(gameProfile2, true);
            return gameProfile2;
        } else {
            GameProfile gameProfile2 = gameProfileCache.get(input.getName());
            if (gameProfile2 == null)
                return input;
            Property property = Iterables.getFirst(gameProfile2.getProperties().get("textures"), null);
            if (property == null)
                gameProfile2 = sessionService.fillProfileProperties(gameProfile2, true);
            return gameProfile2;
        }
    }

    /**
     * Caches the results of {@link SkullBlockEntity#updateGameprofile(GameProfile)}. Use {@link SkinHelper#updateGameProfileAsync(GameProfile)} to fill the profile without blocking the current thread.
     *
     * @param input The input game profile
     * @return The filled game profile with properties
     */
    @Nullable
    public static synchronized GameProfile updateGameProfile(@Nullable GameProfile input) {
        if (input == null)
            return null;
        if (Minecraft.getInstance().level == null || StringUtil.isNullOrEmpty(input.getName()))
            return PROFILE_CACHE.computeIfAbsent(input, SkinHelper::guiUpdateGameProfile);
        return PROFILE_CACHE.computeIfAbsent(input, SkullBlockEntity::updateGameprofile);
    }

    /**
     * Caches the results of {@link SkullBlockEntity#updateGameprofile(GameProfile)}.
     *
     * @param input The input game profile
     * @return The filled game profile with properties
     */
    public static CompletableFuture<GameProfile> updateGameProfileAsync(@Nullable GameProfile input) {
        if (input == null)
            return CompletableFuture.completedFuture(null);
        return CompletableFuture.supplyAsync(() -> updateGameProfile(input));
    }

    /**
     * Loads the a texture from the specified player profile.
     *
     * @param input    The profile of the player to load textures for
     * @param type     The type of texture to load
     * @param consumer The listener for when the player texture has been loaded and is ready
     */
    @Environment(EnvType.CLIENT)
    public static void loadPlayerTexture(@Nullable GameProfile input, MinecraftProfileTexture.Type type, Consumer<ResourceLocation> consumer) {
        CompletableFuture.runAsync(() ->
        {
            if (input == null) {
                consumer.accept(DefaultPlayerSkin.getDefaultSkin());
                return;
            }
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = Minecraft.getInstance().getSkinManager().getInsecureSkinInformation(input);
            if (map.containsKey(type)) {
                RenderSystem.recordRenderCall(() -> consumer.accept(Minecraft.getInstance().getSkinManager().registerTexture(map.get(type), type)));
            } else {
                consumer.accept(DefaultPlayerSkin.getDefaultSkin(Player.createPlayerUUID(input)));
            }
        }, Util.backgroundExecutor());
    }
}
