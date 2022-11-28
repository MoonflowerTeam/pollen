package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleManager;

/**
 * A component for controlling particle render properties.
 *
 * @author Ocelot
 * @see CustomParticleManager
 * @since 1.6.0
 */
public interface CustomParticleRenderComponent {

    /**
     * Called before the particle is rendered to set up properties.
     *
     * @param particle The particle to render
     */
    void render(CustomParticle particle);
}
