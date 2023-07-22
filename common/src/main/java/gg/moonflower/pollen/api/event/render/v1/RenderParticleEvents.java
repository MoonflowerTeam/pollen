package gg.moonflower.pollen.api.event.render.v1;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;

public interface RenderParticleEvents {

    Event<Pre> PRE = EventFactory.createLoop(Pre.class);

    Event<Post> POST = EventFactory.createLoop(Post.class);

    /**
     * Called before particles are drawn.
     *
     * @author Ocelot
     * @since 2.0.0
     */
    @FunctionalInterface
    interface Pre {

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
     * @since 2.0.0
     */
    @FunctionalInterface
    interface Post {

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
     * @since 2.0.0
     */
    interface Context {

        /**
         * @return The particle engine instance
         */
        ParticleEngine getParticleEngine();

        /**
         * Inserts the specified render type to draw last.
         *
         * @param type The render type to add
         */
        void addRenderType(ParticleRenderType type);
    }
}
