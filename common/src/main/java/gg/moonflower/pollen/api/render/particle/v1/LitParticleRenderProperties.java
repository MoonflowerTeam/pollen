package gg.moonflower.pollen.api.render.particle.v1;

import gg.moonflower.pinwheel.api.particle.render.ParticleRenderProperties;

/**
 * Minecraft-specific render properties that have lighting.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface LitParticleRenderProperties extends ParticleRenderProperties {

    /**
     * @return The packed light UV coordinates
     */
    int getPackedLight();

    /**
     * Sets the light UV coordinates
     *
     * @param packedLight The new packed light
     */
    void setPackedLight(int packedLight);
}
