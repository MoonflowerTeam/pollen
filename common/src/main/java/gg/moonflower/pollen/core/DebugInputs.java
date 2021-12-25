package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.client.util.TextureDownloader;
import gg.moonflower.pollen.api.event.events.client.InputEvents;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
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
                Util.sequence(result).thenRunAsync(() -> Util.getPlatform().openFile(outputFolder.toFile()), Minecraft.getInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
