package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.component.ParticleAppearanceLightingComponent;
import gg.moonflower.pinwheel.api.particle.render.ParticleRenderProperties;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.LitParticleRenderProperties;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleRenderComponent;
import net.minecraft.client.Camera;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ParticleAppearanceLightingComponentImpl extends BedrockParticleComponentImpl implements BedrockParticleRenderComponent {

    public ParticleAppearanceLightingComponentImpl(BedrockParticle particle, ParticleAppearanceLightingComponent data) {
        super(particle);
    }

    @Override
    public void render(Camera camera, float partialTicks) {
        ParticleRenderProperties properties = this.particle.getRenderProperties();
        if (properties instanceof LitParticleRenderProperties litProperties) {
            litProperties.setPackedLight(this.particle.getPackedLight());
        }
    }
}
