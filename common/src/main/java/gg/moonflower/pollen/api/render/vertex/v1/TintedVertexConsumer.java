package gg.moonflower.pollen.api.render.vertex.v1;

import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pollen.impl.render.vertex.TintedVertexConsumerImpl;

/**
 * Tints the color passed in from {@link #color(int, int, int, int)} with a tint.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface TintedVertexConsumer extends VertexConsumer {

    /**
     * Resets the currently set tint.
     */
    default TintedVertexConsumer resetTint() {
        return this.tint(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Sets the tint to apply to the delegate.
     *
     * @param color The tint to apply
     */
    default TintedVertexConsumer tint(int color) {
        return this.tint(((color >> 16) & 0xFF) / 255F, ((color >> 8) & 0xFF) / 255F, (color & 0xFF) / 255F, ((color >> 24) & 0xFF) / 255F);
    }

    /**
     * Sets the tint to apply to the delegate.
     *
     * @param red   The red tint
     * @param green The green tint
     * @param blue  The blue tint
     * @param alpha The alpha tint
     */
    TintedVertexConsumer tint(float red, float green, float blue, float alpha);

    /**
     * Creates a new tinted consumer from the specified consumer.
     *
     * @param consumer The consumer to add tinting to
     * @return A new tinted consumer
     */
    static TintedVertexConsumer tinted(VertexConsumer consumer) {
        return new TintedVertexConsumerImpl(consumer);
    }
}
