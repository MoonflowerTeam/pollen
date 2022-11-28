package gg.moonflower.pollen.api.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Ocelot
 * @since 1.6.0
 */
public class CustomParticleOption implements ParticleOptions {

    public static final ParticleOptions.Deserializer<CustomParticleOption> DESERIALIZER = new ParticleOptions.Deserializer<>() {
        public CustomParticleOption fromCommand(ParticleType<CustomParticleOption> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            return new CustomParticleOption(particleType, ResourceLocation.read(stringReader));
        }

        public CustomParticleOption fromNetwork(ParticleType<CustomParticleOption> particleType, FriendlyByteBuf buf) {
            return new CustomParticleOption(particleType, buf.readResourceLocation());
        }
    };
    private final ParticleType<CustomParticleOption> type;
    private final ResourceLocation name;

    public static Codec<CustomParticleOption> codec(ParticleType<CustomParticleOption> particleType) {
        return ResourceLocation.CODEC.xmap(name -> new CustomParticleOption(particleType, name), blockParticleOption -> blockParticleOption.name);
    }

    public CustomParticleOption(ParticleType<CustomParticleOption> particleType, ResourceLocation name) {
        this.type = particleType;
        this.name = name;
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.name);
    }

    @Override
    public String writeToString() {
        return Registry.PARTICLE_TYPE.getKey(this.getType()) + " " + this.name;
    }

    @Override
    public ParticleType<CustomParticleOption> getType() {
        return this.type;
    }

    public ResourceLocation getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.writeToString();
    }
}
