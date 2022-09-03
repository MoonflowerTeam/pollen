package gg.moonflower.pollen.impl.pinwheel.geometry;

import com.mojang.logging.LogUtils;
import gg.moonflower.pollen.api.pinwheel.v1.BackgroundLoader;
import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryModel;
import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryModelData;
import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryModelParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

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

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FOLDER = "pinwheel/geometry/";

    @Override
    public CompletableFuture<Map<ResourceLocation, GeometryModel>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() ->
        {
            Map<ResourceLocation, GeometryModel> modelLocations = new HashMap<>();
            for (ResourceLocation modelLocation : resourceManager.listResources(FOLDER, name -> name.endsWith(".json"))) {
                try (Resource resource = resourceManager.getResource(modelLocation)) {
                    GeometryModelData[] models = GeometryModelParser.parseModel(IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8));
                    for (GeometryModelData model : models) {
                        ResourceLocation id = new ResourceLocation(modelLocation.getNamespace(), model.description().identifier());
                        if (modelLocations.put(id, GeometryModel.create(model)) != null)
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
