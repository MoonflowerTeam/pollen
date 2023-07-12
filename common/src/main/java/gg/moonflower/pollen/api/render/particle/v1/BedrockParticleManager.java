package gg.moonflower.pollen.api.render.particle.v1;

import gg.moonflower.pinwheel.api.particle.ParticleData;
import gg.moonflower.pollen.api.render.util.v1.BackgroundLoader;
import gg.moonflower.pollen.impl.render.particle.BedrockParticleManagerImpl;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

/**
 * Loads and manages all Bedrock particle definitions.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface BedrockParticleManager {

    /**
     * Adds the specified particle loader.
     *
     * @param loader The loader to add
     */
    static void addLoader(BackgroundLoader<Map<ResourceLocation, ParticleData>> loader) {
        BedrockParticleManagerImpl.addLoader(loader);
    }

    /**
     * Fetches a particle by the specified name.
     *
     * @param location The name of the particle
     * @return The particle found or {@link ParticleData#EMPTY} if there was no particle
     */
    static ParticleData getParticle(ResourceLocation location) {
        return BedrockParticleManagerImpl.getParticle(location);
    }

    /**
     * Checks if a particle is registered under the specified name.
     *
     * @param location The name of the particle
     * @return Whether that particles exists
     */
    static boolean hasParticle(ResourceLocation location) {
        return BedrockParticleManagerImpl.hasParticle(location);
    }
}
