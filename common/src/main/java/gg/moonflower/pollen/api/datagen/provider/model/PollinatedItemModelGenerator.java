package gg.moonflower.pollen.api.datagen.provider.model;

import com.google.gson.JsonElement;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Base implementation of {@link PollinatedModelGenerator} for items. Items for blocks are generally automatically created.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class PollinatedItemModelGenerator implements PollinatedModelGenerator {

    private final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;

    public PollinatedItemModelGenerator(BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput) {
        this.modelOutput = modelOutput;
    }

    public BiConsumer<ResourceLocation, Supplier<JsonElement>> getModelOutput() {
        return modelOutput;
    }

    protected void generateFlatItem(Item item, ModelTemplate modelTemplate) {
        modelTemplate.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(item), this.modelOutput);
    }

    protected void generateFlatItem(Item item, String modelLocationSuffix, ModelTemplate modelTemplate) {
        modelTemplate.create(ModelLocationUtils.getModelLocation(item, modelLocationSuffix), TextureMapping.layer0(TextureMapping.getItemTexture(item, modelLocationSuffix)), this.modelOutput);
    }

    protected void generateFlatItem(Item item, Item layerZeroItem, ModelTemplate modelTemplate) {
        modelTemplate.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(layerZeroItem), this.modelOutput);
    }
}
