package gg.moonflower.pollen.core.client.entitlement.type;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.core.client.entitlement.Entitlement;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@ApiStatus.Internal
public class Halo extends AbstractHalo {

    public static final Codec<Halo> CODEC = HaloData.CODEC.xmap(Halo::new, halo -> halo.data);

    private final HaloData data;
    private final String[] modelUrl;
    private ResourceLocation textureKey;

    public Halo(HaloData data) {
        this.data = data;
        this.modelUrl = new String[]{data.getModelUrl()};
    }

    @Override
    protected Entitlement copyData() {
        return new Halo(this.data);
    }

    @Override
    public Type getType() {
        return Type.HALO;
    }

    @Override
    public String[] getModelUrls() {
        return modelUrl;
    }

    @Override
    public void registerTextures(BiConsumer<ResourceLocation, GeometryModelTextureTable> textureConsumer) {
        textureConsumer.accept(this.getRegistryName(), this.data.getTextureTable());
        textureConsumer.accept(new ResourceLocation(this.getRegistryName().getNamespace(), this.getRegistryName().getPath() + "_emissive"), new GeometryModelTextureTable(this.data.getTextureTable().getTextureDefinitions().entrySet().stream().collect(Collectors.<Map.Entry<String, GeometryModelTexture[]>, String, GeometryModelTexture[]>toMap(Map.Entry::getKey, entry -> {
            GeometryModelTexture[] textures = new GeometryModelTexture[entry.getValue().length];
            for (int i = 0; i < textures.length; i++) {
                GeometryModelTexture texture = entry.getValue()[i];
                textures[i] = GeometryModelTexture.texture(texture).setGlowing(true).build();
            }
            return textures;
        }))));
    }

    @Nullable
    @Override
    public ResourceLocation getTextureKey() {
        if (this.textureKey == null)
            this.textureKey = this.getRegistryName();
        return this.textureKey;
    }

    @Nullable
    @Override
    protected HaloData getData() {
        return data;
    }

    @Override
    public void setEmissive(boolean emissive) {
        super.setEmissive(emissive);
        this.textureKey = new ResourceLocation(this.getRegistryName().getNamespace(), this.getRegistryName().getPath() + (this.isEmissive() ? "_emissive" : ""));
    }
}
