package gg.moonflower.pollen.api.render.particle.v1.component;

/**
 * A component that needs updates every tick.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface BedrockParticleTickComponent extends BedrockParticleComponent {

    /**
     * Called every tick to update this component.
     */
    void tick();
}
