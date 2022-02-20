package gg.moonflower.pollen.api.event.events.registry.client;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.NotNull;

/**
 * Registers particle factories when it is safe to do so.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@FunctionalInterface
public interface ParticleFactoryRegistryEvent {

    PollinatedEvent<ParticleFactoryRegistryEvent> EVENT = EventRegistry.createLoop(ParticleFactoryRegistryEvent.class);

    /**
     * Registers all particle factories.
     *
     * @param registry The registry to register into
     */
    void registerParticles(Registry registry);

    /**
     * Manages registering particle providers for custom particle factories.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    interface Registry {

        /**
         * Registers a simple provider for particles with the specified type. No sprites are loaded for this particle.
         *
         * @param type     The type of particle to register
         * @param provider The simple provider to create new particles
         * @param <T>      The options for the type of particle
         */
        <T extends ParticleOptions> void register(ParticleType<T> type, ParticleProvider<T> provider);

        /**
         * Registers a factory for particles with the specified type. These providers are created when they are able.
         * <p>Particle sprites for {@link SpriteSet} are loaded from <code>domain:particles/particle_name.json</code>.
         *
         * @param type    The type of particle to register
         * @param factory A factory to create a particle after the sprite set has been loaded
         * @param <T>     The options for the type of particle
         */
        <T extends ParticleOptions> void register(ParticleType<T> type, Factory<T> factory);
    }

    /**
     * Creates new particle providers from {@link SpriteSet}.
     *
     * @param <T> The options for the type of particle to create
     * @author Ocelot
     * @since 1.0.0
     */
    @FunctionalInterface
    interface Factory<T extends ParticleOptions> {

        /**
         * Creates a new provider with the specified set of sprites.
         *
         * @param sprites The sprites loaded from <code>domain:particles/particle_name.json</code>.
         * @return A new particle provider
         */
        @NotNull
        ParticleProvider<T> create(SpriteSet sprites);
    }
}
