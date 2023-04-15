package gg.moonflower.pollen.impl.render.vertex;

import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pollen.api.render.vertex.v1.TintedVertexConsumer;
import gg.moonflower.pollen.api.render.vertex.v1.WrapperVertexConsumer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class TintedVertexConsumerImpl extends WrapperVertexConsumer implements TintedVertexConsumer {

    private float red;
    private float green;
    private float blue;
    private float alpha;

    public TintedVertexConsumerImpl(VertexConsumer delegate) {
        super(delegate);
        this.resetTint();
    }

    @Override
    public TintedVertexConsumerImpl tint(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return super.color((int) (red * this.red), (int) (green * this.green), (int) (blue * this.blue), (int) (alpha * this.alpha));
    }
}
