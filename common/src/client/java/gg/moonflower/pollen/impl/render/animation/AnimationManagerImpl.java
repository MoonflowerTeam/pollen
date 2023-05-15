package gg.moonflower.pollen.impl.render.animation;

import dev.architectury.registry.ReloadListenerRegistry;
import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryModelManager;
import gg.moonflower.pollen.api.render.util.v1.BackgroundLoader;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@ApiStatus.Internal
public final class AnimationManagerImpl {

    private static final Logger LOGGER = LogManager.getLogger(GeometryModelManager.class);
    private static final Reloader RELOADER = new Reloader();
    private static final Set<BackgroundLoader<Map<ResourceLocation, AnimationData>>> LOADERS = new HashSet<>();
    private static final Map<ResourceLocation, AnimationData> ANIMATIONS = new HashMap<>();

    public static void init() {
        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, RELOADER, new ResourceLocation(Pollen.MOD_ID, "animation_manager"));
        addLoader(new LocalAnimationLoader());
    }

    public static void addLoader(BackgroundLoader<Map<ResourceLocation, AnimationData>> loader) {
        LOADERS.add(loader);
    }

    public static AnimationData getAnimation(ResourceLocation location) {
        return ANIMATIONS.computeIfAbsent(location, key -> {
            LOGGER.warn("Unknown animation with key '{}'", location);
            return AnimationData.EMPTY;
        });
    }

    private static class Reloader implements PreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            Map<ResourceLocation, AnimationData> animationData = new HashMap<>();
            return CompletableFuture.allOf(LOADERS.stream().map(modelLoader -> modelLoader.reload(resourceManager, backgroundExecutor, gameExecutor).thenAcceptAsync(animations -> {
                for (Map.Entry<ResourceLocation, AnimationData> entry : animations.entrySet()) {
                    if (animationData.put(entry.getKey(), entry.getValue()) != null) {
                        LOGGER.warn("Duplicate animation: " + entry.getKey());
                    }
                }
            }, gameExecutor)).toArray(CompletableFuture[]::new)).thenCompose(stage::wait).thenRunAsync(() -> {
                LOGGER.info("Loaded " + animationData.size() + " animations.");
                ANIMATIONS.clear();
                ANIMATIONS.putAll(animationData);
            }, gameExecutor);
        }
    }
}
