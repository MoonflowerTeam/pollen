package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import gg.moonflower.pollen.api.particle.PollenParticleComponents;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.common.particle.render.CustomParticleRenderProperties;

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
    public void render(CustomParticle particle) {
        CustomParticleRenderProperties properties = particle.getRenderProperties();
        if (properties != null) {
            properties.setPackedLight(particle.getPackedLight());
        }
    }

    @Override
    public CustomParticleComponentType<?> type() {
        return PollenParticleComponents.PARTICLE_APPEARANCE_LIGHTING.get();
    }
}
