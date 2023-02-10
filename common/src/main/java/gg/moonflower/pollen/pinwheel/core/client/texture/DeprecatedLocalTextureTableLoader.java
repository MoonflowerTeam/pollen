package gg.moonflower.pollen.pinwheel.core.client.texture;

import com.google.gson.Gson;
import gg.moonflower.pollen.pinwheel.api.client.texture.TextureTableLoader;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelParser;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import gg.moonflower.pollen.pinwheel.core.client.geometry.LocalGeometryModelLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Ocelot
 */
@Deprecated
@ApiStatus.Internal
public class DeprecatedLocalTextureTableLoader implements TextureTableLoader {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new Gson();
    private final Map<ResourceLocation, GeometryModelTextureTable> textures;
    private final String folder;
    private String[] hashTables;

    public DeprecatedLocalTextureTableLoader() {
        this("textures/geometry");
    }

    public DeprecatedLocalTextureTableLoader(@Nullable String folder) {
        this.textures = new HashMap<>();
        this.folder = folder == null || folder.isEmpty() ? "" : folder + "/";
        this.hashTables = new String[0];
    }

    @Override
    public void addTextures(BiConsumer<ResourceLocation, GeometryModelTextureTable> textureConsumer) {
        this.textures.forEach(textureConsumer);
    }

    @Override
    public void addHashTables(Consumer<String> hashTableConsumer) {
        for (String hashTable : this.hashTables)
            hashTableConsumer.accept(hashTable);
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() ->
        {
            Set<ResourceLocation> deprecatedFiles = new HashSet<>();
            Map<ResourceLocation, GeometryModelTextureTable> textureLocations = new HashMap<>();
            for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(this.folder, name -> name.getPath().endsWith(".json")).entrySet()) {
                ResourceLocation textureTableLocation = entry.getKey();
                ResourceLocation textureTableName = new ResourceLocation(textureTableLocation.getNamespace(), textureTableLocation.getPath().substring(this.folder.length(), textureTableLocation.getPath().length() - 5));
                if (textureTableName.getPath().equals("hash_tables"))
                    continue;

                try (BufferedReader reader = entry.getValue().openAsReader()) {
                    GeometryModelTextureTable table = GeometryModelParser.parseTextures(reader);
                    // Validate there are no online textures
                    table.getTextureDefinitions().forEach((name, textures) -> {
                        for (GeometryModelTexture texture : textures)
                            if (texture.getType() == GeometryModelTexture.Type.ONLINE)
                                throw new IllegalArgumentException(name + " uses unsupported texture type: " + texture.getType().name().toLowerCase());
                    });
                    textureLocations.put(textureTableName, table);
                    deprecatedFiles.add(textureTableName);
                } catch (Exception e) {
                    LOGGER.error("Failed to load texture table '" + textureTableName + "'", e);
                }
            }

            deprecatedFiles.stream().map(ResourceLocation::getNamespace).forEach(namespace -> LOGGER.error("Mod: " + namespace + " is using deprecated Pollen textures. Texture tables should be relocated to 'assets/" + namespace + "/pinwheel/textures/geometry'"));
            return textureLocations;
        }, backgroundExecutor).thenAcceptBothAsync(CompletableFuture.supplyAsync(() ->
        {
            Set<ResourceLocation> deprecatedFiles = new HashSet<>();
            Set<String> hashTables = new HashSet<>();
            for (String domain : resourceManager.getNamespaces()) {
                ResourceLocation hashTableLocation = new ResourceLocation(domain, this.folder + "hash_tables.json");
                Optional<Resource> hashTableResource = resourceManager.getResource(hashTableLocation);
                if (hashTableResource.isEmpty())
                    continue;

                try (BufferedReader reader = hashTableResource.get().openAsReader()) {
                    hashTables.addAll(Arrays.asList(GSON.fromJson(reader, String[].class)));
                    deprecatedFiles.add(hashTableLocation);
                } catch (Exception e) {
                    LOGGER.error("Failed to load texture hash table for " + domain, e);
                }
            }

            deprecatedFiles.stream().map(ResourceLocation::getNamespace).forEach(namespace -> LOGGER.error("Mod: " + namespace + " is using deprecated Pollen textures. 'assets/" + namespace + "/" + this.folder + "hash_tables.json' should be relocated to 'assets/" + namespace + "/pinwheel/textures/geometry/hash_tables.json'"));
            return hashTables.toArray(new String[0]);
        }, backgroundExecutor), (textureLocations, hashTables) ->
        {
            this.textures.clear();
            this.textures.putAll(textureLocations);
            this.hashTables = hashTables;
        }, gameExecutor).thenCompose(stage::wait);
    }
}
