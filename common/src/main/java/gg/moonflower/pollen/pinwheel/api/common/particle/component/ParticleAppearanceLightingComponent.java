package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.common.particle.render.CustomParticleRenderProperties;
import net.minecraft.client.Camera;

/**
 * Component that applies lighting to a particle.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleAppearanceLightingComponent implements CustomParticleComponent, CustomParticleRenderComponent {

    public ParticleAppearanceLightingComponent(JsonElement json) {
    }

    @Override
    public void tick(CustomParticle particle) {
    }

    @Override
    public void render(CustomParticle particle, Camera camera, float partialTicks) {
        CustomParticleRenderProperties properties = particle.getRenderProperties();
        if (properties != null) {
            properties.setPackedLight(particle.getPackedLight());
        }
    }
}
