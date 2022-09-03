package gg.moonflower.pollen.impl.pinwheel.animation;

import com.mojang.logging.LogUtils;
import gg.moonflower.pollen.api.pinwheel.v1.animation.AnimationParser;
import gg.moonflower.pollen.api.pinwheel.v1.BackgroundLoader;
import gg.moonflower.pollen.api.pinwheel.v1.animation.AnimationData;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class LocalAnimationLoader implements BackgroundLoader<Map<ResourceLocation, AnimationData>> {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final String folder;

    public LocalAnimationLoader() {
        this.folder = "pinwheel/animations/";
    }

    @Override
    public CompletableFuture<Map<ResourceLocation, AnimationData>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() ->
        {
            Map<ResourceLocation, AnimationData> animationData = new HashMap<>();
            try {
                for (ResourceLocation animationLocation : resourceManager.listResources(this.folder, name -> name.endsWith(".json"))) {
                    try (Resource resource = resourceManager.getResource(animationLocation)) {
                        AnimationData[] animations = AnimationParser.parse(new InputStreamReader(resource.getInputStream()));
                        for (AnimationData animation : animations) {
                            ResourceLocation id = new ResourceLocation(animationLocation.getNamespace(), animation.name());
                            if (animationData.put(id, animation) != null)
                                LOGGER.warn("Duplicate animation: " + id);
                        }
                    } catch (Exception e) {
                        LOGGER.error("Failed to load animation: " + animationLocation.getNamespace() + ":" + animationLocation.getPath().substring(this.folder.length(), animationLocation.getPath().length() - 5), e);
                    }
                }
            } catch (ResourceLocationException e) {
                LOGGER.error("Failed to load animations from: " + this.folder, e);
            }
            return animationData;
        }, backgroundExecutor);
    }
}
