package gg.moonflower.pollen.impl.render.geometry;

import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pinwheel.api.texture.ModelTexture;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryBufferSource;
import gg.moonflower.pollen.api.render.geometry.v1.GeometryModelManager;
import gg.moonflower.pollen.impl.render.shader.PollenShaderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GeometryBufferSourceWrapper implements GeometryBufferSource {

    private final MultiBufferSource delegate;

    public GeometryBufferSourceWrapper(MultiBufferSource delegate) {
        this.delegate = delegate;
    }

    @Override
    public VertexConsumer getBuffer(ModelTexture texture) {
        return this.delegate.getBuffer(GeometryRenderTypes.entity(texture, GeometryModelManager.getModel()));
    }
}
