package gg.moonflower.pollen.core.client.loader;

import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import gg.moonflower.pollen.core.client.entitlement.TexturedEntitlement;
import gg.moonflower.pollen.pinwheel.api.client.texture.TextureTableLoader;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Ocelot
 */
public class CosmeticTextureLoader implements TextureTableLoader {

    @Override
    public void addTextures(BiConsumer<ResourceLocation, GeometryModelTextureTable> textureConsumer) {
        EntitlementManager.getAllEntitlements().filter(entitlement -> entitlement instanceof TexturedEntitlement).forEach(entitlement -> ((TexturedEntitlement) entitlement).registerTextures(textureConsumer));
    }

    @Override
    public void addHashTables(Consumer<String> hashTableConsumer) {
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.completedFuture(null);
    }
}
