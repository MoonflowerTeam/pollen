package gg.moonflower.pollen.pinwheel.core.client.animation;

import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationData;
import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationParser;
import gg.moonflower.pollen.pinwheel.api.common.util.BackgroundLoader;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
public class DeprecatedLocalAnimationLoader implements BackgroundLoader<Map<ResourceLocation, AnimationData>> {

    private static final Logger LOGGER = LogManager.getLogger("LocalAnimationLoader");

    private final String folder;

    public DeprecatedLocalAnimationLoader() {
        this.folder = "animations/";
    }

    @Override
    public CompletableFuture<Map<ResourceLocation, AnimationData>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() ->
        {
            Set<ResourceLocation> deprecatedFiles = new HashSet<>();
            Map<ResourceLocation, AnimationData> animationData = new HashMap<>();
            try {
                for (Map.Entry<ResourceLocation, Resource> entry : resourceManager.listResources(this.folder, name -> name.getPath().endsWith(".json")).entrySet()) {
                    ResourceLocation animationLocation = entry.getKey();
                    try (BufferedReader reader = entry.getValue().openAsReader()) {
                        AnimationData[] animations = AnimationParser.parse(reader);
                        for (AnimationData animation : animations) {
                            ResourceLocation id = new ResourceLocation(animationLocation.getNamespace(), animation.getName());
                            if (animationData.put(id, animation) != null)
                                LOGGER.warn("Duplicate animation: " + id);
                        }
                        deprecatedFiles.add(animationLocation);
                    } catch (Exception ignored) {
                    }
                }
            } catch (ResourceLocationException e) {
                LOGGER.error("Failed to load animations from: " + this.folder, e);
            }

            deprecatedFiles.stream().map(ResourceLocation::getNamespace).forEach(namespace -> LOGGER.error("Mod: " + namespace + " is using deprecated Pollen animations. Animations should be relocated to 'assets/" + namespace + "/pinwheel/animations'"));
            return animationData;
        }, backgroundExecutor);
    }
}
