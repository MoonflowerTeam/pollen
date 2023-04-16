package gg.moonflower.pollen.impl.particle;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class PollenParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Pollen.MOD_ID, Registry.PARTICLE_TYPE_REGISTRY);

    public static final RegistrySupplier<ParticleType<BedrockParticleOption>> CUSTOM = PARTICLE_TYPES.register("custom", () -> new ParticleType<>(true, BedrockParticleOption.DESERIALIZER) {
        @Override
        public Codec<BedrockParticleOption> codec() {
            return BedrockParticleOption.codec(this);
        }
    });
    public static final RegistrySupplier<ParticleType<BedrockParticleOption>> CUSTOM_INSTANCE = PARTICLE_TYPES.register("custom_instance", () -> new ParticleType<>(false, BedrockParticleOption.DESERIALIZER) {
        @Override
        public Codec<BedrockParticleOption> codec() {
            return BedrockParticleOption.codec(this);
        }
    });

    private PollenParticles() {
    }
}
