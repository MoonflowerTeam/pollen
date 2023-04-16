package gg.moonflower.pollen.core;

import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.registries.Registries;
import gg.moonflower.pollen.api.event.render.v1.RenderParticleEvents;
import gg.moonflower.pollen.api.registry.particle.v1.BedrockParticleComponents;
import gg.moonflower.pollen.impl.render.geometry.GeometryModelManagerImpl;
import gg.moonflower.pollen.impl.render.geometry.texture.GeometryTextureManagerImpl;
import gg.moonflower.pollen.impl.render.particle.BedrockParticleManagerImpl;
import gg.moonflower.pollen.impl.render.particle.instance.BedrockParticleEmitterImpl;
import gg.moonflower.pollen.impl.render.particle.instance.BedrockParticleInstanceImpl;
import gg.moonflower.pollen.impl.particle.PollenParticles;
import gg.moonflower.pollen.impl.render.shader.PollenShaderTypes;

public class PollenClient {

    public static void init() {
        GeometryModelManagerImpl.init();
        GeometryTextureManagerImpl.init();
        BedrockParticleManagerImpl.init();
        PollenShaderTypes.init();
        BedrockParticleComponents.COMPONENTS.register();
        ParticleProviderRegistry.register(PollenParticles.CUSTOM, new BedrockParticleEmitterImpl.Provider());
        RenderParticleEvents.PRE.register((context, bufferSource, lightTexture, camera, partialTicks) -> context.addRenderType(BedrockParticleInstanceImpl.GEOMETRY_SHEET));
    }

    public static void postInit() {
    }
}
