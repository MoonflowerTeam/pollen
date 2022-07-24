package gg.moonflower.pollen.core;

import com.google.common.base.Stopwatch;
import gg.moonflower.pollen.api.client.util.TextureDownloader;
import gg.moonflower.pollen.api.event.events.client.InputEvents;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import gg.moonflower.pollen.core.client.profile.ProfileManager;
import gg.moonflower.pollen.pinwheel.api.client.animation.AnimationManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.lwjgl.opengl.GL11C.glIsTexture;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class DebugInputs {

    static void init() {
        InputEvents.KEY_INPUT_EVENT.register(DebugInputs::onKeyInput);
    }

    private static boolean onKeyInput(int key, int scanCode, int action, int modifiers) {
        if (Platform.isProduction() || Minecraft.getInstance().screen instanceof ChatScreen || action != GLFW.GLFW_PRESS)
            return false;
        if (key == GLFW.GLFW_KEY_BACKSLASH) {
            Player player = Minecraft.getInstance().player;
            if ((modifiers & GLFW.GLFW_MOD_ALT) > 0) {
                if (player == null)
                    return true;

                final Stopwatch timer = Stopwatch.createStarted();
                UUID id = Minecraft.getInstance().getUser().getGameProfile().getId();
                ProfileManager.clearCache(id);
                EntitlementManager.clearCache();
                CompletableFuture.allOf(GeometryModelManager.reload(false), GeometryTextureManager.reload(false), AnimationManager.reload(false), EntitlementManager.reload(true, CompletableFuture::completedFuture).thenCompose(__ -> EntitlementManager.getEntitlementsFuture(id))).handle((u, t) -> {
                    timer.stop();
                    Minecraft.getInstance().gui.getChat().addMessage(Component.empty().append(Component.translatable("debug.prefix").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)).append(" Took " + timer + " to reload entitlements"));
                    return u;
                });

                return true;
            }

            try {
                Path outputFolder = Paths.get(Minecraft.getInstance().gameDirectory.toURI()).resolve("debug-out");
                if (!Files.exists(outputFolder)) {
                    Files.createDirectories(outputFolder);
                } else {
                    Files.walkFileTree(outputFolder, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            Files.delete(file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }

                List<CompletableFuture<?>> result = new LinkedList<>();
                for (int i = 0; i < 1024; i++)
                    if (glIsTexture(i))
                        result.add(TextureDownloader.save(Integer.toString(i), outputFolder, i));
                CompletableFuture.allOf(result.toArray(new CompletableFuture[0])).thenRunAsync(() -> Util.getPlatform().openFile(outputFolder.toFile()), Minecraft.getInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
