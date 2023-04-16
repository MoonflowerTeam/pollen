package gg.moonflower.pollen.api.registry.particle.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import gg.moonflower.pinwheel.api.particle.component.ParticleComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleManager;

/**
 * Deserializes particle data components from JSON.
 *
 * @param <T> The type of particle component to create
 * @author Ocelot
 * @see BedrockParticleManager
 * @since 2.0.0
 */
@FunctionalInterface
public interface BedrockParticleDataFactory<T extends ParticleComponent> {

    /**
     * Creates a new particle component for the specified particle.
     *
     * @param data The data to parse the component from
     * @return The created component
     * @throws JsonParseException If there is an error parsing the component
     */
    T create(JsonElement data) throws JsonParseException;
}
