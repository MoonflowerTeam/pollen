package gg.moonflower.pollen.pinwheel.api.common.particle.render;

import com.mojang.math.Quaternion;

/**
 * Generic interface for specifying render properties.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public interface CustomParticleRenderProperties {

    Quaternion getRotation();

    float getRed();

    float getGreen();

    float getBlue();

    float getAlpha();

    int getPackedLight();

    void setRotation(Quaternion rotation);

    void setRed(float red);

    void setGreen(float green);

    void setBlue(float blue);

    void setAlpha(float alpha);

    void setPackedLight(int packedLight);

    default void setColor(float red, float green, float blue, float alpha) {
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
        this.setAlpha(alpha);
    }

    default void setColor(int color) {
        this.setRed((float) (color >> 16 & 0xFF) / 255F);
        this.setGreen((float) (color >> 8 & 0xFF) / 255F);
        this.setBlue((float) (color & 0xFF) / 255F);
        this.setAlpha((float) (color >> 24 & 0xFF) / 255F);
    }

    boolean canRender();
}
