package gg.moonflower.pollen.pinwheel.core.client.texture;

import gg.moonflower.pollen.pinwheel.api.client.texture.TextureTableLoader;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class StaticTextureTableLoader implements TextureTableLoader {

    private final ResourceLocation location;
    private final GeometryModelTextureTable texture;
    private final String hashTable;

    public StaticTextureTableLoader(ResourceLocation location, GeometryModelTextureTable texture, @Nullable String hashTable) {
        this.location = location;
        this.texture = texture;
        this.hashTable = hashTable;
    }

    @Override
    public void addTextures(BiConsumer<ResourceLocation, GeometryModelTextureTable> textureConsumer) {
        textureConsumer.accept(this.location, this.texture);
    }

    @Override
    public void addHashTables(Consumer<String> hashTableConsumer) {
        if (this.hashTable != null)
            hashTableConsumer.accept(this.hashTable);
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.completedFuture(null);
    }
}
