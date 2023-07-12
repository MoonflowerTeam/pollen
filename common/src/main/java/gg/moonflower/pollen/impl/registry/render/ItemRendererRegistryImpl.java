package gg.moonflower.pollen.impl.registry.render;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.render.item.v1.DynamicItemRenderer;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public final class ItemRendererRegistryImpl {

    private static final Map<Item, ModelResourceLocation> MODELS = new ConcurrentHashMap<>();

    private ItemRendererRegistryImpl() {
    }

    @ExpectPlatform
    public static void registerRenderer(ItemLike item, DynamicItemRenderer renderer) {
        Pollen.expect();
    }

    public static void registerHandModel(ItemLike item, ModelResourceLocation handModel) {
        MODELS.put(item.asItem(), handModel);
    }

    @Nullable
    public static ModelResourceLocation getHandModel(Item item) {
        return MODELS.get(item);
    }
}
