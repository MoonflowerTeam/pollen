package gg.moonflower.pollen.core.client.entitlement.type;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

@ApiStatus.Internal
public class Halo extends AbstractHalo {

    public static final Codec<Halo> CODEC = HaloData.CODEC.xmap(Halo::new, halo -> halo.data);

    private final HaloData data;
    private final String[] modelUrl;

    public Halo(HaloData data) {
        this.data = data;
        this.modelUrl = new String[]{data.getModelUrl()};
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
    }

    @Nullable
    @Override
    public ResourceLocation getTextureKey() {
        return this.getRegistryName();
    }

    @Nullable
    @Override
    protected HaloData getData() {
        return data;
    }
}
