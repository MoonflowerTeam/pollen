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
    private int animationTick;

    protected AnimatedPathfinder(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.effectHandler = new AnimationEffectHandler(this);
        this.animationState = AnimationState.EMPTY;
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
    public AnimationState getAnimationState() {
        return animationState;
    }

    @Override
    public void setAnimationState(AnimationState state) {
        this.onAnimationStop(this.animationState);
        this.animationState = state;
        this.setAnimationTick(0);
    }

    @Override
    public AnimationEffectHandler getAnimationEffects() {
        return effectHandler;
    }
}
