package gg.moonflower.pollen.impl.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class BedrockParticleOption implements ParticleOptions {

    public static final Deserializer<BedrockParticleOption> DESERIALIZER = new Deserializer<>() {
        public BedrockParticleOption fromCommand(ParticleType<BedrockParticleOption> particleType, StringReader stringReader) throws CommandSyntaxException {
            stringReader.expect(' ');
            return new BedrockParticleOption(particleType, ResourceLocation.read(stringReader));
        }

        public BedrockParticleOption fromNetwork(ParticleType<BedrockParticleOption> particleType, FriendlyByteBuf buf) {
            return new BedrockParticleOption(particleType, buf.readResourceLocation());
        }
    };
    private final ParticleType<BedrockParticleOption> type;
    private final ResourceLocation name;

    public static Codec<BedrockParticleOption> codec(ParticleType<BedrockParticleOption> particleType) {
        return ResourceLocation.CODEC.xmap(name -> new BedrockParticleOption(particleType, name), blockParticleOption -> blockParticleOption.name);
    }

    public BedrockParticleOption(ParticleType<BedrockParticleOption> particleType, ResourceLocation name) {
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
    public ParticleType<BedrockParticleOption> getType() {
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
