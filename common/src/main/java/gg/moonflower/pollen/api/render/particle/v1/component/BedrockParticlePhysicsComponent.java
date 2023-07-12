package gg.moonflower.pollen.api.render.particle.v1.component;

/**
 * A component that needs updates every physics tick.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface BedrockParticlePhysicsComponent extends BedrockParticleComponent {

    /**
     * Called every physics tick to update this component.
     */
    void physicsTick();
}
