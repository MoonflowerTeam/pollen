package gg.moonflower.pollen.api.registry.particle.v1;

import gg.moonflower.pinwheel.api.particle.component.ParticleComponent;

public record BedrockParticleComponentType<T extends ParticleComponent>(BedrockParticleDataFactory<T> dataFactory, BedrockParticleComponentFactory<T> componentFactory) {
}
