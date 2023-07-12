package gg.moonflower.pollen.impl.render.animation;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.AnimationParser;
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
public class LocalAnimationLoader implements BackgroundLoader<Map<ResourceLocation, AnimationData>> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String FOLDER = "pinwheel/animations";

    @Override
    public CompletableFuture<Map<ResourceLocation, AnimationData>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() ->
        {
            Map<ResourceLocation, AnimationData> animationData = new HashMap<>();
            for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(FOLDER, resourceLocation -> resourceLocation.getPath().endsWith(".json")).entrySet()) {
                ResourceLocation animationLocation = entry.getKey();

                try (BufferedReader reader = entry.getValue().openAsReader()) {
                    AnimationData[] animations = AnimationParser.parse(reader);
                    for (AnimationData animation : animations) {
                        ResourceLocation id = new ResourceLocation(animationLocation.getNamespace(), animation.name());
                        if (animationData.put(id, animation) != null)
                            LOGGER.warn("Duplicate animation: " + id);
                    }
                } catch (Exception e) {
                    LOGGER.error("Failed to load animation file '" + animationLocation.getNamespace() + ":" + animationLocation.getPath().substring(FOLDER.length() + 1, animationLocation.getPath().length() - 5) + "'", e);
                }
            }
            return animationData;
        }, backgroundExecutor);
    }
}
