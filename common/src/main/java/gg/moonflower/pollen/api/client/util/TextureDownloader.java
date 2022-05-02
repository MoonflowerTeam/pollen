package gg.moonflower.pollen.api.client.util;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.lwjgl.opengl.GL11C.GL_RGBA;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_HEIGHT;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_WIDTH;
import static org.lwjgl.opengl.GL11C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11C.glBindTexture;
import static org.lwjgl.opengl.GL11C.glGetTexImage;
import static org.lwjgl.opengl.GL11C.glGetTexLevelParameteri;
import static org.lwjgl.opengl.GL11C.glGetTexParameteri;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12C.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;

/**
 * Properly downloads textures from OpenGL and writes them to a file asynchronously.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class TextureDownloader {

    private TextureDownloader() {
    }

    /**
     * Writes the specified texture to file with the specified name. If the texture specifies mipmap levels, then the file name will be <code>name-#.png</code>.
     *
     * @param name         The name of the file to save to
     * @param outputFolder The folder to place the file in
     * @param texture      The id of the texture to download
     * @return A future for when all texture levels have been downloaded and saved
     */
    public static CompletableFuture<?> save(String name, Path outputFolder, int texture) {
        glBindTexture(GL_TEXTURE_2D, texture);
        int base = glGetTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_BASE_LEVEL);
        int max = glGetTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL);
        if (max == 1000)
            max = 0;

        List<CompletableFuture<?>> result = new ArrayList<>(max - base + 1);
        for (int level = base; level <= max; level++) {
            Path outputFile = outputFolder.resolve(name + (base == max ? "" : "-" + level) + ".png");
            if (!Files.exists(outputFile)) {
                try {
                    Files.createFile(outputFile);
                } catch (Exception e) {
                    result.add(Util.failedFuture(e));
                    continue;
                }
            }

            int width = glGetTexLevelParameteri(GL_TEXTURE_2D, level, GL_TEXTURE_WIDTH);
            int height = glGetTexLevelParameteri(GL_TEXTURE_2D, level, GL_TEXTURE_HEIGHT);

            ByteBuffer image = BufferUtils.createByteBuffer(width * height * 4);
            glGetTexImage(GL_TEXTURE_2D, level, GL_RGBA, GL_UNSIGNED_BYTE, image);

            CompletableFuture<?> future = CompletableFuture.runAsync(() ->
            {
                if (!stbi_write_png(outputFile.toString(), width, height, 4, image, 0))
                    throw new CompletionException(new IOException("Failed to write image to: " + outputFile));
            }, Util.ioPool());
            result.add(future);
        }
        return CompletableFuture.allOf(result.toArray(CompletableFuture[]::new));
    }

    /**
     * Writes the specified texture to file with the specified name. If the texture specifies mipmap levels, then the file name will be <code>name-#.png</code>.
     *
     * @param name         The name of the file to save to
     * @param outputFolder The folder to place the file in
     * @param texture      The texture object to download
     * @return A future for when all texture levels have been downloaded and saved
     */
    public static CompletableFuture<?> save(String name, Path outputFolder, AbstractTexture texture) {
        return save(name, outputFolder, texture.getId());
    }

    /**
     * Writes the specified texture to file with the specified name. If the texture specifies mipmap levels, then the file name will be <code>name-#.png</code>. The missing texture will be written if there is no texture with that id.
     *
     * @param name         The name of the file to save to
     * @param outputFolder The folder to place the file in
     * @param texture      The id of the registered texture object
     * @return A future for when all texture levels have been downloaded and saved
     */
    public static CompletableFuture<?> save(String name, Path outputFolder, ResourceLocation texture) {
        AbstractTexture abstractTexture = Minecraft.getInstance().getTextureManager().getTexture(texture);
        return save(name, outputFolder, abstractTexture != null ? abstractTexture : MissingTextureAtlasSprite.getTexture());
    }
}
