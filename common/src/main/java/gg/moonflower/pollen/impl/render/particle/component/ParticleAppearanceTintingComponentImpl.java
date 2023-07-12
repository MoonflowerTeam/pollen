package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.particle.component.ParticleAppearanceTintingComponent;
import gg.moonflower.pinwheel.api.particle.render.ParticleRenderProperties;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleRenderComponent;
import net.minecraft.client.Camera;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ParticleAppearanceTintingComponentImpl extends BedrockParticleComponentImpl implements BedrockParticleRenderComponent {

    private final ParticleAppearanceTintingComponent data;

    public ParticleAppearanceTintingComponentImpl(BedrockParticle particle, ParticleAppearanceTintingComponent data) {
        super(particle);
        this.data = data;
    }

    @Override
    public void render(Camera camera, float partialTicks) {
        ParticleRenderProperties properties = this.particle.getRenderProperties();
        if (properties != null) {
            MolangEnvironment environment = this.particle.getEnvironment();
            properties.setColor(this.data.red().get(this.particle, environment), this.data.green().get(this.particle, environment), this.data.blue().get(this.particle, environment), this.data.alpha().get(this.particle, environment));
        }
    }
}
