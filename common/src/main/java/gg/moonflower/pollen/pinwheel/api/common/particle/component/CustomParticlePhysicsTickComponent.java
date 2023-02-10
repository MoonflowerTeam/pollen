package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleManager;

/**
 * A component that needs updates every physics tick.
 *
 * @author Ocelot
 * @see CustomParticleManager
 * @since 1.6.0
 */
public interface CustomParticlePhysicsTickComponent {

    /**
     * Called every physics tick to update this component.
     *
     * @param particle The particle to tick
     */
    void physicsTick(CustomParticle particle);
}
