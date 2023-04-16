package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleComponent;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

@ApiStatus.Internal
public abstract class BedrockParticleComponentImpl implements BedrockParticleComponent {

    protected final BedrockParticle particle;

    protected BedrockParticleComponentImpl(BedrockParticle particle) {
        this.particle = particle;
    }

    protected BedrockParticlePhysics getPhysics() {
        return Objects.requireNonNull(this.particle.getPhysics(), "physics");
    }
}
