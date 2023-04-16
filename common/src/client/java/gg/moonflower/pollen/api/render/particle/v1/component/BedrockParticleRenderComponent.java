package gg.moonflower.pollen.api.render.particle.v1.component;

import net.minecraft.client.Camera;

/**
 * A component for controlling particle render properties.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface BedrockParticleRenderComponent {

    /**
     * Called before the particle is rendered to set up properties.
     *
     * @param camera       The camera rendering the particle
     * @param partialTicks The percentage from last tick to this tick
     */
    void render(Camera camera, float partialTicks);
}
