package gg.moonflower.pollen.api.render.geometry.v1;

import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pinwheel.api.texture.ModelTexture;
import net.minecraft.client.renderer.MultiBufferSource;

@FunctionalInterface
public interface GeometryBufferSource {

    VertexConsumer getBuffer(ModelTexture texture);

    static GeometryBufferSource wrap(MultiBufferSource source) {

    }
}
