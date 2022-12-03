package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleManager;
import net.minecraft.client.Camera;

/**
 * A component for controlling particle render properties.
 *
 * @author Ocelot
 * @see CustomParticleManager
 * @since 1.6.0
 */
public interface CustomParticleRenderComponent {

    /**
     * Called before the particle is rendered to set up properties.
     *
     * @param particle     The particle to render
     * @param camera       The camera rendering the particle
     * @param partialTicks The percentage from last tick to this tick
     */
    void render(CustomParticle particle, Camera camera, float partialTicks);
}
