package gg.moonflower.pollen.pinwheel.api.common.animation;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;

/**
 * <p>An implementation of {@link AnimatedEntity} for basic entities.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class AnimatedPathfinder extends PathfinderMob implements AnimatedEntity {
    private final AnimationEffectHandler effectHandler;
    private AnimationState animationState;
    private AnimationState transitionAnimationState;
    private int animationTick;
    private int animationTransitionTick;
    private int animationTransitionLength;

    protected AnimatedPathfinder(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.effectHandler = new AnimationEffectHandler(this);
        this.animationState = AnimationState.EMPTY;
        this.transitionAnimationState = AnimationState.EMPTY;
    }

    @Override
    public int getAnimationTick() {
        return animationTick;
    }

    @Override
    public void setAnimationTick(int tick) {
        this.animationTick = tick;
    }

    @Override
    public int getAnimationTransitionTick() {
        return animationTransitionTick;
    }

    @Override
    public void setAnimationTransitionTick(int animationTransitionTick) {
        this.animationTransitionTick = animationTransitionTick;
    }

    @Override
    public int getAnimationTransitionLength() {
        return animationTransitionLength;
    }

    @Override
    public void setAnimationTransitionLength(int animationTransitionLength) {
        this.animationTransitionLength = animationTransitionLength;
    }

    @Override
    public AnimationState getAnimationState() {
        return animationState;
    }

    @Override
    public AnimationState getTransitionAnimationState() {
        return transitionAnimationState;
    }

    @Override
    public void setAnimationState(AnimationState state) {
        this.onAnimationStop(this.animationState);
        this.animationState = state;
        this.setAnimationTick(0);
        this.setAnimationTransitionLength(0);
    }

    @Override
    public void setTransitionAnimationState(AnimationState state) {
        this.transitionAnimationState = state;
    }

    @Override
    public AnimationEffectHandler getAnimationEffects() {
        return effectHandler;
    }
}
