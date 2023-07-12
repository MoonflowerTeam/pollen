package gg.moonflower.pollen.api.render.vertex.v1;

import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * A base for any custom vertex consumer implementations.
 * This doesn't break during chaining like {@link net.minecraft.client.renderer.SpriteCoordinateExpander}.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public class WrapperVertexConsumer implements VertexConsumer {

    protected final VertexConsumer delegate;

    public WrapperVertexConsumer(VertexConsumer delegate) {
        this.delegate = delegate;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        this.delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        this.delegate.color(red, green, blue, alpha);
        return this;
    }

    @Override
    public VertexConsumer uv(float u, float v) {
        this.delegate.uv(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlayCoords(int u, int v) {
        this.delegate.overlayCoords(u, v);
        return this;
    }

    @Override
    public VertexConsumer uv2(int u, int v) {
        this.delegate.uv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        this.delegate.normal(x, y, z);
        return this;
    }

    @Override
    public void endVertex() {
        this.delegate.endVertex();
    }

    @Override
    public void defaultColor(int red, int green, int blue, int alpha) {
        this.delegate.defaultColor(red, green, blue, alpha);
    }

    @Override
    public void unsetDefaultColor() {
        this.delegate.unsetDefaultColor();
    }
}
