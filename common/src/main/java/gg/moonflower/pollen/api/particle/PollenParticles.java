package gg.moonflower.pollen.api.particle;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.RegistryValue;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Built-in Pollen particle types and implementations.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public final class PollenParticles {

    @ApiStatus.Internal
    public static final PollinatedRegistry<ParticleType<?>> PARTICLE_TYPES = PollinatedRegistry.create(Registry.PARTICLE_TYPE, Pollen.MOD_ID);

    public static final RegistryValue<ParticleType<CustomParticleOption>> CUSTOM = PARTICLE_TYPES.register("custom", () -> new ParticleType<>(true, CustomParticleOption.DESERIALIZER) {
        @Override
        public Codec<CustomParticleOption> codec() {
            return CustomParticleOption.codec(this);
        }
    });
    public static final RegistryValue<ParticleType<CustomParticleOption>> CUSTOM_INSTANCE = PARTICLE_TYPES.register("custom_instance", () -> new ParticleType<>(false, CustomParticleOption.DESERIALIZER) {
        @Override
        public Codec<CustomParticleOption> codec() {
            return CustomParticleOption.codec(this);
        }
    });

    private PollenParticles() {
    }
}
