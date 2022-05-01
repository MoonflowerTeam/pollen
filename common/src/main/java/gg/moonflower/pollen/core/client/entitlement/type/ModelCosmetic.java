package gg.moonflower.pollen.core.client.entitlement.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import gg.moonflower.pollen.core.client.entitlement.RenderableCosmetic;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.BiConsumer;

@ApiStatus.Internal
public class ModelCosmetic extends SimpleCosmetic implements RenderableCosmetic {

    public static final Codec<ModelCosmetic> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("modelUrl").forGetter(cosmetic -> cosmetic.getModelUrls()[0]),
            Codec.STRING.fieldOf("modelKey").forGetter(cosmetic -> Objects.requireNonNull(cosmetic.getModelKey()).getPath()),
            GeometryModelTextureTable.CODEC.fieldOf("textureTable").forGetter(ModelCosmetic::getTextureTable)
    ).apply(instance, ModelCosmetic::new));

    private final String[] modelUrl;
    private final ResourceLocation modelKey;
    private final GeometryModelTextureTable textureTable;

    public ModelCosmetic(String modelUrl, String modelKey, GeometryModelTextureTable textureTable) {
        this.modelUrl = new String[]{modelUrl};
        this.modelKey = new ResourceLocation(Pollen.MOD_ID, modelKey);
        this.textureTable = textureTable;
    }

    @Override
    protected Entitlement copyData() {
        return new ModelCosmetic(this.modelUrl[0], this.modelKey.getPath(), this.textureTable);
    }

    @Override
    public Type getType() {
        return Type.COSMETIC;
    }

    @Override
    public void registerTextures(BiConsumer<ResourceLocation, GeometryModelTextureTable> textureConsumer) {
        textureConsumer.accept(this.getRegistryName(), this.textureTable);
    }

    @Override
    public ResourceLocation getTextureKey() {
        return this.getRegistryName();
    }

    @Override
    public String[] getModelUrls() {
        return modelUrl;
    }

    @Override
    public ResourceLocation getModelKey() {
        return modelKey;
    }

    public GeometryModelTextureTable getTextureTable() {
        return textureTable;
    }
}
