package gg.moonflower.pollen.impl.render.geometry.texture;

import gg.moonflower.pinwheel.api.geometry.GeometryModelParser;
import gg.moonflower.pinwheel.api.texture.ModelTexture;
import gg.moonflower.pinwheel.api.texture.TextureTable;
import gg.moonflower.pollen.api.render.util.v1.BackgroundLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class LocalTextureTableLoader implements BackgroundLoader<Map<ResourceLocation, TextureTable>> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FOLDER = "pinwheel/textures";

    @Override
    public CompletableFuture<Map<ResourceLocation, TextureTable>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, TextureTable> textureLocations = new HashMap<>();
            for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(FOLDER, name -> name.getPath().endsWith(".json")).entrySet()) {
                ResourceLocation location = entry.getKey();
                ResourceLocation textureTableName = new ResourceLocation(location.getNamespace(), location.getPath().substring(FOLDER.length() + 1, location.getPath().length() - 5));

                try (BufferedReader reader = entry.getValue().openAsReader()) {
                    TextureTable table = GeometryModelParser.parseTextures(reader);

                    // Validate there are no online textures
                    table.getTextureDefinitions().forEach((name, textures) -> {
                        for (ModelTexture texture : textures) {
                            if (texture.type() == ModelTexture.Type.ONLINE) {
                                throw new IllegalArgumentException(name + " uses unsupported texture type: " + texture.type().name().toLowerCase());
                            }
                        }
                    });

                    textureLocations.put(textureTableName, table);
                } catch (Exception e) {
                    LOGGER.error("Failed to load texture table '" + textureTableName + "'", e);
                }
            }
            return textureLocations;
        }, backgroundExecutor);
    }
}
