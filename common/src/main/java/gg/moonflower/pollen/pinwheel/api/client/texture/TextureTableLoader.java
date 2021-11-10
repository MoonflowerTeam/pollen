package gg.moonflower.pollen.pinwheel.api.client.texture;

import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <p>Reloads and uploads textures to {@link GeometryTextureManager}.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface TextureTableLoader extends PreparableReloadListener {

    /**
     * Adds all textures to the provided consumer.
     *
     * @param textureConsumer The consumer for textures
     */
    void addTextures(BiConsumer<ResourceLocation, GeometryModelTextureTable> textureConsumer);

    /**
     * Adds all hash tables to the provided consumer.
     *
     * @param hashTableConsumer The consumer for hash tables
     */
    void addHashTables(Consumer<String> hashTableConsumer);
}
