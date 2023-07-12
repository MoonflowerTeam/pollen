package gg.moonflower.pollen.api.registry.particle.v1;

import com.google.gson.JsonParseException;
import gg.moonflower.pinwheel.api.particle.component.ParticleComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleManager;

/**
 * A component that can be added to a custom particle or particle emitter. Use {@link #particle(BedrockParticleComponentFactory)} and {@link #emitter(BedrockParticleComponentFactory)} to specify specific components.
 *
 * @param <T> The type of particle component to create
 * @author Ocelot
 * @see BedrockParticleManager
 * @since 2.0.0
 */
@FunctionalInterface
public interface BedrockParticleComponentFactory<T extends ParticleComponent> {

    /**
     * Creates a new particle component for the specified particle.
     *
     * @param data The data to parse the component from
     * @return The created component
     * @throws JsonParseException If there is an error parsing the component
     */
    BedrockParticleComponent create(BedrockParticle particle, T data) throws JsonParseException;

    /**
     * Whether this component can be used by the specified particle.
     *
     * @param particle The particle to check
     * @return Whether it is valid
     */
    default boolean isValid(BedrockParticle particle) {
        return true;
    }

    /**
     * Creates a particle type for particles specifically.
     *
     * @param type The type to wrap
     * @param <T>  The type of particle component to create
     * @return A type that only supports particles
     */
    static <T extends ParticleComponent> BedrockParticleComponentFactory<T> particle(BedrockParticleComponentFactory<T> type) {
        return new BedrockParticleComponentFactory<T>() {
            @Override
            public BedrockParticleComponent create(BedrockParticle particle, T data) throws JsonParseException {
                return type.create(particle, data);
            }

            @Override
            public boolean isValid(BedrockParticle particle) {
                return !(particle instanceof BedrockParticleEmitter);
            }
        };
    }

    /**
     * Creates a particle type for emitters specifically.
     *
     * @param type The type to wrap
     * @param <T>  The type of particle component to create
     * @return A type that only supports emitters
     */
    static <T extends ParticleComponent> BedrockParticleComponentFactory<T> emitter(BedrockParticleComponentFactory<T> type) {
        return new BedrockParticleComponentFactory<T>() {
            @Override
            public BedrockParticleComponent create(BedrockParticle particle, T data) throws JsonParseException {
                return type.create(particle, data);
            }

            @Override
            public boolean isValid(BedrockParticle particle) {
                return particle instanceof BedrockParticleEmitter;
            }
        };
    }
}
