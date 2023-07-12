package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.EmitterLocalSpaceComponent;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleComponent;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class EmitterLocalSpaceComponentImpl implements BedrockParticleComponent {

    private final EmitterLocalSpaceComponent data;

    public EmitterLocalSpaceComponentImpl(BedrockParticle particle, EmitterLocalSpaceComponent data) {
        this.data = data;
    }
}
