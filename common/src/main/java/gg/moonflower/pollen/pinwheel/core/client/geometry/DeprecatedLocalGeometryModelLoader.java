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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Ocelot
 */
@Deprecated
@ApiStatus.Internal
public final class DeprecatedLocalGeometryModelLoader implements BackgroundLoader<Map<ResourceLocation, GeometryModel>> {

    private static final Logger LOGGER = LogManager.getLogger("LocalGeometryModelLoader");
    private static final String FOLDER = "models/geometry/";

    @Override
    public CompletableFuture<Map<ResourceLocation, GeometryModel>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() ->
        {
            Set<ResourceLocation> deprecatedFiles = new HashSet<>();
            Map<ResourceLocation, GeometryModel> modelLocations = new HashMap<>();
            for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(FOLDER, name -> name.getPath().endsWith(".json")).entrySet()) {
                ResourceLocation modelLocation = entry.getKey();
                if ("geckolib3".equals(modelLocation.getNamespace())) continue; // Explicitly ignore geckolib3
                try (InputStream stream = entry.getValue().open()) {
                    GeometryModelData[] models = GeometryModelParser.parseModel(IOUtils.toString(stream, StandardCharsets.UTF_8));
                    for (GeometryModelData model : models) {
                        ResourceLocation id = new ResourceLocation(modelLocation.getNamespace(), model.getDescription().getIdentifier());
                        if (modelLocations.put(id, model.create()) != null)
                            LOGGER.warn("Duplicate geometry model with id '" + id + "'");
                    }
                    deprecatedFiles.add(modelLocation);
                } catch (Exception ignored) {
                }
            }

            deprecatedFiles.stream().map(ResourceLocation::getNamespace).forEach(namespace -> LOGGER.error("Mod: " + namespace + " is using deprecated Pollen models. Geometry models should be relocated to 'assets/" + namespace + "/" + LocalGeometryModelLoader.FOLDER.substring(0, LocalGeometryModelLoader.FOLDER.length() - 1) + "'"));
            LOGGER.info("Loaded " + modelLocations.size() + " geometry models.");
            return modelLocations;
        }, backgroundExecutor);
    }
}
