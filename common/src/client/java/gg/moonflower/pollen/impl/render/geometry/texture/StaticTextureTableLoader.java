package gg.moonflower.pollen.impl.render.geometry.texture;

import gg.moonflower.pinwheel.api.texture.TextureTable;
import gg.moonflower.pollen.api.render.util.v1.BackgroundLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class StaticTextureTableLoader implements BackgroundLoader<Map<ResourceLocation, TextureTable>> {

    private final Map<ResourceLocation, TextureTable> staticTextures;

    public StaticTextureTableLoader() {
        this.staticTextures = new HashMap<>();
    }

    public void addTexture(ResourceLocation location, TextureTable texture) {
        this.staticTextures.put(location, texture);
    }

    @Override
    public CompletableFuture<Map<ResourceLocation, TextureTable>> reload(ResourceManager resourceManager, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.completedFuture(Collections.unmodifiableMap(this.staticTextures));
    }
}
