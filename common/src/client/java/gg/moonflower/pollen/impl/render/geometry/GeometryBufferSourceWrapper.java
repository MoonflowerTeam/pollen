package gg.moonflower.pollen.impl.render.geometry;

import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pinwheel.api.texture.ModelTexture;
import gg.moonflower.pollen.api.pinwheelbridge.v1.PinwheelBridge;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryBufferSource;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryModelManager;
import gg.moonflower.pollen.api.render.vertex.v1.TintedVertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
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
            return GeometryRenderTypes.particle(texture.key(), GeometryModelManager.getAtlas(), null);
        }
        return GeometryRenderTypes.entity(texture.key(), GeometryModelManager.getAtlas(), null);
    }

    @Override
    public VertexConsumer getBuffer(ModelTexture texture) {
        TextureAtlasSprite sprite = GeometryModelManager.getAtlas().getSprite(PinwheelBridge.toLocation(texture.location()));
        return sprite.wrap(TintedVertexConsumer.tinted(this.delegate.getBuffer(this.getRenderType(texture))).tint(texture.color()));
    }
}
