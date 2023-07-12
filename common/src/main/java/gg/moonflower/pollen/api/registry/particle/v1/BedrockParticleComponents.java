package gg.moonflower.pollen.api.registry.particle.v1;

import dev.architectury.registry.registries.RegistrySupplier;
import gg.moonflower.pinwheel.api.particle.component.*;
import gg.moonflower.pollen.api.registry.wrapper.v1.PollinatedRegistry;
import gg.moonflower.pollen.impl.render.particle.component.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

/**
 * <p>Bedrock particles components that can be added using JSON.</p>
 * <p>New components can be registered using {@link #register(ResourceLocation, BedrockParticleDataFactory, BedrockParticleComponentFactory)}.
 * Components use a static data class for loading from JSON, then another component class that actually implements the behavior.
 * View some of the built-in components to understand how to implement more components.</p>
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface BedrockParticleComponents {

    ResourceKey<Registry<BedrockParticleComponentType<?>>> COMPONENTS_REGISTRY = ResourceKey.createRegistryKey(new ResourceLocation("particle_components"));
    PollinatedRegistry<BedrockParticleComponentType<?>> COMPONENTS = PollinatedRegistry.create(COMPONENTS_REGISTRY);

    RegistrySupplier<BedrockParticleComponentType<ParticleLifetimeEventComponent>> EMITTER_LIFETIME_EVENTS = register("emitter_lifetime_events", ParticleLifetimeEventComponent::deserialize, BedrockParticleComponentFactory.emitter(BedrockParticleLifetimeEventComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<EmitterLifetimeExpressionComponent>> EMITTER_LIFETIME_EXPRESSION = register("emitter_lifetime_expression", EmitterLifetimeExpressionComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterLifetimeExpressionComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<EmitterLifetimeLoopingComponent>> EMITTER_LIFETIME_LOOPING = register("emitter_lifetime_looping", EmitterLifetimeLoopingComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterLifetimeLoopingComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<EmitterLifetimeOnceComponent>> EMITTER_LIFETIME_ONCE = register("emitter_lifetime_once", EmitterLifetimeOnceComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterLifetimeOnceComponentImpl::new));

    RegistrySupplier<BedrockParticleComponentType<EmitterRateInstantComponent>> EMITTER_RATE_INSTANT = register("emitter_rate_instant", EmitterRateInstantComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterRateInstantComponentImpl::new));
    // No emitter_rate_manual
    RegistrySupplier<BedrockParticleComponentType<EmitterRateSteadyComponent>> EMITTER_RATE_STEADY = register("emitter_rate_steady", EmitterRateSteadyComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterRateSteadyComponentImpl::new));

    RegistrySupplier<BedrockParticleComponentType<EmitterShapeDiscComponent>> EMITTER_SHAPE_DISC = register("emitter_shape_disc", EmitterShapeDiscComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterShapeComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<EmitterShapeBoxComponent>> EMITTER_SHAPE_BOX = register("emitter_shape_box", EmitterShapeBoxComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterShapeComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<EmitterShapePointComponent>> EMITTER_SHAPE_CUSTOM = register("emitter_shape_custom", EmitterShapePointComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterShapeComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<EmitterShapeEntityBoxComponent>> EMITTER_SHAPE_ENTITY_AABB = register("emitter_shape_entity_aabb", EmitterShapeEntityBoxComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterShapeComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<EmitterShapePointComponent>> EMITTER_SHAPE_POINT = register("emitter_shape_point", EmitterShapePointComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterShapeComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<EmitterShapeSphereComponent>> EMITTER_SHAPE_SPHERE = register("emitter_shape_sphere", EmitterShapeSphereComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterShapeComponentImpl::new));

    RegistrySupplier<BedrockParticleComponentType<EmitterInitializationComponent>> EMITTER_INITIALIZATION = register("emitter_initialization", EmitterInitializationComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterInitializationComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<EmitterLocalSpaceComponent>> EMITTER_LOCAL_SPACE = register("emitter_local_space", EmitterLocalSpaceComponent::deserialize, BedrockParticleComponentFactory.emitter(EmitterLocalSpaceComponentImpl::new));

    RegistrySupplier<BedrockParticleComponentType<ParticleAppearanceBillboardComponent>> PARTICLE_APPEARANCE_BILLBOARD = register("particle_appearance_billboard", ParticleAppearanceBillboardComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleAppearanceBillboardComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<ParticleAppearanceLightingComponent>> PARTICLE_APPEARANCE_LIGHTING = register("particle_appearance_lighting", ParticleAppearanceLightingComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleAppearanceLightingComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<ParticleAppearanceTintingComponent>> PARTICLE_APPEARANCE_TINTING = register("particle_appearance_tinting", ParticleAppearanceTintingComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleAppearanceTintingComponentImpl::new));

    RegistrySupplier<BedrockParticleComponentType<ParticleInitialSpeedComponent>> PARTICLE_INITIAL_SPEED = register("particle_initial_speed", ParticleInitialSpeedComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleInitialSpeedComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<ParticleInitialSpinComponent>> PARTICLE_INITIAL_SPIN = register("particle_initial_spin", ParticleInitialSpinComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleInitialSpinComponentImpl::new));

    RegistrySupplier<BedrockParticleComponentType<ParticleExpireInBlocksComponent>> PARTICLE_EXPIRE_IN_BLOCKS_EXPRESSION = register("particle_expire_if_in_blocks", ParticleExpireInBlocksComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleExpireInBlocksComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<ParticleExpireNotInBlocksComponent>> PARTICLE_EXPIRE_NOT_IN_BLOCKS_EXPRESSION = register("particle_expire_if_not_in_blocks", ParticleExpireNotInBlocksComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleExpireNotInBlocksComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<ParticleLifetimeEventComponent>> PARTICLE_LIFETIME_EVENTS = register("particle_lifetime_events", ParticleLifetimeEventComponent::deserialize, BedrockParticleComponentFactory.particle(BedrockParticleLifetimeEventComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<ParticleLifetimeExpressionComponent>> PARTICLE_LIFETIME_EXPRESSION = register("particle_lifetime_expression", ParticleLifetimeExpressionComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleLifetimeExpressionComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<ParticleKillPlaneComponent>> PARTICLE_KILL_PLANE = register("particle_kill_plane", ParticleKillPlaneComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleKillPlaneComponentImpl::new));

    RegistrySupplier<BedrockParticleComponentType<ParticleMotionCollisionComponent>> PARTICLE_MOTION_COLLISION = register("particle_motion_collision", ParticleMotionCollisionComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleMotionCollisionComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<ParticleMotionDynamicComponent>> PARTICLE_MOTION_DYNAMIC = register("particle_motion_dynamic", ParticleMotionDynamicComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleMotionDynamicComponentImpl::new));
    RegistrySupplier<BedrockParticleComponentType<ParticleMotionParametricComponent>> PARTICLE_MOTION_PARAMETRIC = register("particle_motion_parametric", ParticleMotionParametricComponent::deserialize, BedrockParticleComponentFactory.particle(ParticleMotionParametricComponentImpl::new));

    /**
     * Registers a new particle component with the specified name.
     *
     * @param key              The name used to identify the component in JSON. It will be <code>minecraft:key</code>
     * @param dataFactory      The factory to create the static component data
     * @param componentFactory The factory to create the per-particle component that implements behavior
     * @param <T>              The type of data class to use
     * @return A new component type for chaining
     */
    static <T extends ParticleComponent> RegistrySupplier<BedrockParticleComponentType<T>> register(String key, BedrockParticleDataFactory<T> dataFactory, BedrockParticleComponentFactory<T> componentFactory) {
        return register(new ResourceLocation(key), dataFactory, componentFactory);
    }

    /**
     * Registers a new particle component with the specified name.
     *
     * @param key              The name used to identify the component in JSON
     * @param dataFactory      The factory to create the static component data
     * @param componentFactory The factory to create the per-particle component that implements behavior
     * @param <T>              The type of data class to use
     * @return A new component type for chaining
     */
    static <T extends ParticleComponent> RegistrySupplier<BedrockParticleComponentType<T>> register(ResourceLocation key, BedrockParticleDataFactory<T> dataFactory, BedrockParticleComponentFactory<T> componentFactory) {
        return COMPONENTS.register(key, () -> new BedrockParticleComponentType<>(dataFactory, componentFactory));
    }
}
