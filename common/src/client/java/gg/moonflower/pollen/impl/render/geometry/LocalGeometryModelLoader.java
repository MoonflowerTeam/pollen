package gg.moonflower.pollen.impl.render.geometry;

import gg.moonflower.pinwheel.api.geometry.GeometryModel;
import gg.moonflower.pinwheel.api.geometry.GeometryModelData;
import gg.moonflower.pinwheel.api.geometry.GeometryModelParser;
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
public final class LocalGeometryModelLoader implements BackgroundLoader<Map<ResourceLocation, GeometryModel>> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FOLDER = "pinwheel/geometry";

    @Override
    public CompletableFuture<Map<ResourceLocation, GeometryModel>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, GeometryModel> modelLocations = new HashMap<>();
            for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(FOLDER, resourceLocation -> resourceLocation.getPath().endsWith(".json")).entrySet()) {
                ResourceLocation modelLocation = entry.getKey();

                try (BufferedReader reader = entry.getValue().openAsReader()) {
                    GeometryModelData[] models = GeometryModelParser.parseModel(reader);
                    for (GeometryModelData model : models) {
                        ResourceLocation id = new ResourceLocation(modelLocation.getNamespace(), model.description().identifier());
                        if (modelLocations.put(id, new BedrockGeometryModel(model)) != null) {
                            LOGGER.warn("Duplicate geometry model with id '" + id + "'");
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load geometry file '" + modelLocation.getNamespace() + ":" + modelLocation.getPath().substring(FOLDER.length() + 1, modelLocation.getPath().length() - 5) + "'", e);
                }
            }
            return modelLocations;
        }, backgroundExecutor);
    }
}
