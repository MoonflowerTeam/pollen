package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleManager;
import org.jetbrains.annotations.Nullable;

/**
 * A component that can be added to a custom particle or particle emitter. Use {@link #particle(CustomParticleComponentType)} and {@link #emitter(CustomParticleComponentType)} to specify specific components.
 *
 * @param <T> The type of particle component to create
 * @author Ocelot
 * @see CustomParticleManager
 * @since 1.6.0
 */
@FunctionalInterface
public interface CustomParticleComponentType<T extends CustomParticleComponent> {

    /**
     * Creates a new particle component for the specified particle.
     *
     * @param data The data to parse the component from
     * @return The created component
     * @throws JsonParseException If there is an error parsing the component
     */
    T create(JsonElement data) throws JsonParseException;

    /**
     * Whether this component can be used by the specified particle.
     *
     * @param particle The particle to check
     * @return Whether it is valid
     */
    default boolean isValid(CustomParticle particle) {
        return true;
    }

    /**
     * Creates a particle type for particles specifically.
     *
     * @param type The type to wrap
     * @param <T>  The type of particle component to create
     * @return A type that only supports particles
     */
    static <T extends CustomParticleComponent> CustomParticleComponentType<T> particle(CustomParticleComponentType<T> type) {
        return new CustomParticleComponentType<T>() {
            @Override
            public T create(JsonElement data) throws JsonParseException {
                return type.create(data);
            }

            @Override
            public boolean isValid(CustomParticle particle) {
                return particle.isParticle();
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
    static <T extends CustomParticleComponent> CustomParticleComponentType<T> emitter(CustomParticleComponentType<T> type) {
        return new CustomParticleComponentType<T>() {
            @Nullable
            @Override
            public T create(JsonElement data) throws JsonParseException {
                return type.create(data);
            }

            @Override
            public boolean isValid(CustomParticle particle) {
                return particle instanceof CustomParticleEmitter;
            }
        };
    }
}
