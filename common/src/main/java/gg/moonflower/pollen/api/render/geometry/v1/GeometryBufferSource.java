package gg.moonflower.pollen.api.render.geometry.v1;

import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pinwheel.api.texture.ModelTexture;
import gg.moonflower.pollen.impl.render.geometry.GeometryBufferSourceWrapper;
import net.minecraft.client.renderer.MultiBufferSource;

/**
 * Retrieves specialized vertex consumers for {@linkplain ModelTexture model textures}.
 *
 * @author Ocelot
 * @since 2.0.0
 */
@FunctionalInterface
public interface GeometryBufferSource {

    /**
     * Retrieves a vertex consumer for the specified texture.
     *
     * @param texture The texture to get the builder for
     * @return The vertex consumer for that texture
     */
    VertexConsumer getBuffer(ModelTexture texture);

    /**
     * Wraps the specified buffer source with the default model render types.
     *
     * @param source The source of vertex consumers
     * @return A new geometry buffer source
     */
    static GeometryBufferSource entity(MultiBufferSource source) {
        return new GeometryBufferSourceWrapper(source, false);
    }

    /**
     * Wraps the specified buffer source with the default model render types.
     *
     * @param source The source of vertex consumers
     * @return A new geometry buffer source
     */
    static GeometryBufferSource particle(MultiBufferSource source) {
        return new GeometryBufferSourceWrapper(source, true);
    }
}
