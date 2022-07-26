package gg.moonflower.pollen.pinwheel.core.client.geometry;

import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModel;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelData;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelParser;
import gg.moonflower.pollen.pinwheel.api.common.util.BackgroundLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public final class LocalGeometryModelLoader implements BackgroundLoader<Map<ResourceLocation, GeometryModel>> {

    private static final Logger LOGGER = LogManager.getLogger();
    static final String FOLDER = "pinwheel/geometry/";

    @Override
    public CompletableFuture<Map<ResourceLocation, GeometryModel>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() ->
        {
            Map<ResourceLocation, GeometryModel> modelLocations = new HashMap<>();
            for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(FOLDER, name -> name.getPath().endsWith(".json")).entrySet()) {
                ResourceLocation modelLocation = entry.getKey();
                try (InputStream stream = entry.getValue().open()) {
                    GeometryModelData[] models = GeometryModelParser.parseModel(IOUtils.toString(stream, StandardCharsets.UTF_8));
                    for (GeometryModelData model : models) {
                        ResourceLocation id = new ResourceLocation(modelLocation.getNamespace(), model.getDescription().getIdentifier());
                        if (modelLocations.put(id, model.create()) != null)
                            LOGGER.warn("Duplicate geometry model with id '" + id + "'");
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load geometry file '" + modelLocation.getNamespace() + ":" + modelLocation.getPath().substring(FOLDER.length(), modelLocation.getPath().length() - 5) + "'", e);
                }
            }

            LOGGER.info("Loaded " + modelLocations.size() + " geometry models.");
            return modelLocations;
        }, backgroundExecutor);
    }
}
