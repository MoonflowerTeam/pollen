package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleManager;

/**
 * A component for custom particle emitters. Local variables can safely be stored here.
 *
 * @author Ocelot
 * @see CustomParticleManager
 * @since 1.6.0
 */
public interface CustomParticleEmitterComponent extends CustomParticleComponent {

    /**
     * Called every tick to update this component.
     *
     * @param emitter The emitter to tick
     */
    void tick(CustomParticleEmitter emitter);

    @Override
    default void tick(CustomParticle particle) {
        this.tick((CustomParticleEmitter) particle);
    }
}
