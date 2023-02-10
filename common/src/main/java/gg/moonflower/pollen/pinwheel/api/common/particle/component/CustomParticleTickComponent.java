package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleManager;
import net.minecraft.client.Camera;

/**
 * A component that needs updates every tick.
 *
 * @author Ocelot
 * @see CustomParticleManager
 * @since 1.6.0
 */
public interface CustomParticleTickComponent {

    /**
     * Called every tick to update this component.
     *
     * @param particle The particle to tick
     */
    void tick(CustomParticle particle);
}
