package gg.moonflower.pollen.api.particle;

import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.*;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Built-in Pollen custom particle components.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class PollenParticleComponents {

    public static final PollinatedRegistry<CustomParticleComponentType<?>> COMPONENTS = PollinatedRegistry.createSimple(new ResourceLocation("particle_components"));

    public static final Supplier<CustomParticleComponentType<ParticleLifetimeEventComponent>> EMITTER_LIFETIME_EVENTS = register("emitter_lifetime_events", CustomParticleComponentType.emitter(ParticleLifetimeEventComponent::new));
    public static final Supplier<CustomParticleComponentType<EmitterLifetimeExpressionComponent>> EMITTER_LIFETIME_EXPRESSION = register("emitter_lifetime_expression", CustomParticleComponentType.emitter(EmitterLifetimeExpressionComponent::new));
    public static final Supplier<CustomParticleComponentType<EmitterLifetimeLoopingComponent>> EMITTER_LIFETIME_LOOPING = register("emitter_lifetime_looping", CustomParticleComponentType.emitter(EmitterLifetimeLoopingComponent::new));
    public static final Supplier<CustomParticleComponentType<EmitterLifetimeOnceComponent>> EMITTER_LIFETIME_ONCE = register("emitter_lifetime_once", CustomParticleComponentType.emitter(EmitterLifetimeOnceComponent::new));

    public static final Supplier<CustomParticleComponentType<EmitterRateInstantComponent>> EMITTER_RATE_INSTANT = register("emitter_rate_instant", CustomParticleComponentType.emitter(EmitterRateInstantComponent::new));
    // No emitter_rate_manual
    public static final Supplier<CustomParticleComponentType<EmitterRateSteadyComponent>> EMITTER_RATE_STEADY = register("emitter_rate_steady", CustomParticleComponentType.emitter(EmitterRateSteadyComponent::new));

    public static final Supplier<CustomParticleComponentType<EmitterShapeDiscComponent>> EMITTER_SHAPE_DISC = register("emitter_shape_disc", CustomParticleComponentType.emitter(EmitterShapeDiscComponent::new));
    public static final Supplier<CustomParticleComponentType<EmitterShapeBoxComponent>> EMITTER_SHAPE_BOX = register("emitter_shape_box", CustomParticleComponentType.emitter(EmitterShapeBoxComponent::new));
    public static final Supplier<CustomParticleComponentType<EmitterShapePointComponent>> EMITTER_SHAPE_CUSTOM = register("emitter_shape_custom", CustomParticleComponentType.emitter(EmitterShapePointComponent::new));
    public static final Supplier<CustomParticleComponentType<EmitterShapeEntityAABBComponent>> EMITTER_SHAPE_ENTITY_AABB = register("emitter_shape_entity_aabb", CustomParticleComponentType.emitter(EmitterShapeEntityAABBComponent::new));
    public static final Supplier<CustomParticleComponentType<EmitterShapePointComponent>> EMITTER_SHAPE_POINT = register("emitter_shape_point", CustomParticleComponentType.emitter(EmitterShapePointComponent::new));
    public static final Supplier<CustomParticleComponentType<EmitterShapeSphereComponent>> EMITTER_SHAPE_SPHERE = register("emitter_shape_sphere", CustomParticleComponentType.emitter(EmitterShapeSphereComponent::new));

    public static final Supplier<CustomParticleComponentType<EmitterInitializationComponent>> EMITTER_INITIALIZATION = register("emitter_initialization", CustomParticleComponentType.emitter(EmitterInitializationComponent::new));
    public static final Supplier<CustomParticleComponentType<EmitterLocalSpaceComponent>> EMITTER_LOCAL_SPACE = register("emitter_local_space", CustomParticleComponentType.emitter(EmitterLocalSpaceComponent::new));

    public static final Supplier<CustomParticleComponentType<ParticleAppearanceBillboardComponent>> PARTICLE_APPEARANCE_BILLBOARD = register("particle_appearance_billboard", CustomParticleComponentType.particle(ParticleAppearanceBillboardComponent::new));
    public static final Supplier<CustomParticleComponentType<ParticleAppearanceLightingComponent>> PARTICLE_APPEARANCE_LIGHTING = register("particle_appearance_lighting", CustomParticleComponentType.particle(ParticleAppearanceLightingComponent::new));
    public static final Supplier<CustomParticleComponentType<ParticleAppearanceTintingComponent>> PARTICLE_APPEARANCE_TINTING = register("particle_appearance_tinting", CustomParticleComponentType.particle(ParticleAppearanceTintingComponent::new));

    public static final Supplier<CustomParticleComponentType<ParticleInitialSpeedComponent>> PARTICLE_INITIAL_SPEED = register("particle_initial_speed", CustomParticleComponentType.particle(ParticleInitialSpeedComponent::new));
    public static final Supplier<CustomParticleComponentType<ParticleInitialSpinComponent>> PARTICLE_INITIAL_SPIN = register("particle_initial_spin", CustomParticleComponentType.particle(ParticleInitialSpinComponent::new));

    public static final Supplier<CustomParticleComponentType<ParticleExpireInBlocksComponent>> PARTICLE_EXPIRE_IN_BLOCKS_EXPRESSION = register("particle_expire_if_in_blocks", CustomParticleComponentType.particle(ParticleExpireInBlocksComponent::new));
    public static final Supplier<CustomParticleComponentType<ParticleExpireNotInBlocksComponent>> PARTICLE_EXPIRE_NOT_IN_BLOCKS_EXPRESSION = register("particle_expire_if_not_in_blocks", CustomParticleComponentType.particle(ParticleExpireNotInBlocksComponent::new));
    public static final Supplier<CustomParticleComponentType<ParticleLifetimeEventComponent>> PARTICLE_LIFETIME_EVENTS = register("particle_lifetime_events", CustomParticleComponentType.particle(ParticleLifetimeEventComponent::new));
    public static final Supplier<CustomParticleComponentType<ParticleLifetimeExpressionComponent>> PARTICLE_LIFETIME_EXPRESSION = register("particle_lifetime_expression", CustomParticleComponentType.particle(ParticleLifetimeExpressionComponent::new));
    public static final Supplier<CustomParticleComponentType<ParticleKillPlane>> PARTICLE_KILL_PLANE = register("particle_kill_plane", CustomParticleComponentType.particle(ParticleKillPlane::new));

    public static final Supplier<CustomParticleComponentType<ParticleMotionCollisionComponent>> PARTICLE_MOTION_COLLISION = register("particle_motion_collision", CustomParticleComponentType.particle(ParticleMotionCollisionComponent::new));
    public static final Supplier<CustomParticleComponentType<ParticleMotionDynamicComponent>> PARTICLE_MOTION_DYNAMIC = register("particle_motion_dynamic", CustomParticleComponentType.particle(ParticleMotionDynamicComponent::new));
    public static final Supplier<CustomParticleComponentType<ParticleMotionParametricComponent>> PARTICLE_MOTION_PARAMETRIC = register("particle_motion_parametric", CustomParticleComponentType.particle(ParticleMotionParametricComponent::new));

    private static <T extends CustomParticleComponent> Supplier<CustomParticleComponentType<T>> register(String name, CustomParticleComponentType<T> factory) {
        return COMPONENTS.register(name, () -> factory);
    }
}
