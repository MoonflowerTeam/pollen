package gg.moonflower.pollen.pinwheel.api.common.particle.render;

import net.minecraft.client.renderer.LightTexture;

/**
 * Generic implementation for colored render properties.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public abstract class ColoredRenderProperties implements CustomParticleRenderProperties {

    private float red;
    private float green;
    private float blue;
    private float alpha;
    private int packedLight;

    public ColoredRenderProperties() {
        this.red = 1.0F;
        this.green = 1.0F;
        this.blue = 1.0F;
        this.alpha = 1.0F;
        this.packedLight = LightTexture.FULL_BRIGHT;
    }

    @Override
    public float getRed() {
        return red;
    }

    @Override
    public float getGreen() {
        return green;
    }

    @Override
    public float getBlue() {
        return blue;
    }

    @Override
    public float getAlpha() {
        return alpha;
    }

    @Override
    public int getPackedLight() {
        return packedLight;
    }

    @Override
    public void setRed(float red) {
        this.red = red;
    }

    @Override
    public void setGreen(float green) {
        this.green = green;
    }

    @Override
    public void setBlue(float blue) {
        this.blue = blue;
    }

    @Override
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    public void setPackedLight(int packedLight) {
        this.packedLight = packedLight;
    }
}
