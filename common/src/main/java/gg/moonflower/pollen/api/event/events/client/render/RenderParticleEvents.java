package gg.moonflower.pollen.api.event.events.client.render;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;

import java.util.List;

public final class RenderParticleEvents {

    public static final PollinatedEvent<RenderParticleEvents.Pre> PRE = EventRegistry.createLoop(RenderParticleEvents.Pre.class);
    public static final PollinatedEvent<RenderParticleEvents.Post> POST = EventRegistry.createLoop(RenderParticleEvents.Post.class);

    private RenderParticleEvents() {
    }

    /**
     * Called before particles are drawn.
     *
     * @author Ocelot
     * @since 1.6.0
     */
    @FunctionalInterface
    public interface Pre {

        /**
         * Sets up how particles will be rendered.
         *
         * @param context      The context for particle rendering
         * @param bufferSource The passed in buffers
         * @param lightTexture The light texture instance
         * @param camera       The perspective to draw from
         * @param partialTicks The percentage from last tick to this tick
         */
        void renderParticlesPre(Context context, MultiBufferSource.BufferSource bufferSource, LightTexture lightTexture, Camera camera, float partialTicks);
    }

    /**
     * Called after all particles have been rendered, but before the render state is reset.
     *
     * @author Ocelot
     * @since 1.6.0
     */
    @FunctionalInterface
    public interface Post {

        /**
         * Sets up the fog colors in the renderer.
         *
         * @param context      The context for particle rendering
         * @param bufferSource The passed in buffers
         * @param lightTexture The light texture instance
         * @param camera       The perspective to draw from
         * @param partialTicks The percentage from last tick to this tick
         */
        void renderParticlesPost(Context context, MultiBufferSource.BufferSource bufferSource, LightTexture lightTexture, Camera camera, float partialTicks);

    }

    /**
     * @author Ocelot
     * @since 1.5.0
     */
    public interface Context {

        /**
         * @return The particle engine instance
         */
        ParticleEngine getParticleEngine();

        /**
         * Inserts the specified render type to draw last.
         *
         * @param type   The render type to add
         */
        void addRenderType(ParticleRenderType type);
    }
}
