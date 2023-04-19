package gg.moonflower.pollen.impl.render.geometry;

import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pinwheel.api.texture.ModelTexture;
import gg.moonflower.pollen.api.render.wrapper.v1.PinwheelWrapper;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryBufferSource;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryTextureManager;
import gg.moonflower.pollen.api.render.vertex.v1.TintedVertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GeometryBufferSourceWrapper implements GeometryBufferSource {

    private final MultiBufferSource delegate;
    private final boolean particle;

    public GeometryBufferSourceWrapper(MultiBufferSource delegate, boolean particle) {
        this.delegate = delegate;
        this.particle = particle;
    }

    private RenderType getRenderType(ModelTexture texture) {
        if (this.particle) {
            return GeometryRenderTypes.particle(texture.key(), GeometryTextureManager.getAtlas(), null);
        }
        return GeometryRenderTypes.entity(texture.key(), GeometryTextureManager.getAtlas(), null);
    }

    @Override
    public VertexConsumer getBuffer(ModelTexture texture) {
        TextureAtlasSprite sprite = GeometryTextureManager.getAtlas().getSprite(PinwheelWrapper.toNative(texture.location()));
        return sprite.wrap(TintedVertexConsumer.tinted(this.delegate.getBuffer(this.getRenderType(texture))).tint(texture.color()));
    }
}
