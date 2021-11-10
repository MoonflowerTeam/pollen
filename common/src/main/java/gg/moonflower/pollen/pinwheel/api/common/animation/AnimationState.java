package gg.moonflower.pollen.pinwheel.api.common.animation;

import net.minecraft.resources.ResourceLocation;

/**
 * <p>An animation state is an array of animations that can play for a specific amount of time. This is to allow the server to know what animation should be playing for state checking.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class AnimationState {
    public static final AnimationState EMPTY = new AnimationState(0);

    private final int tickDuration;
    private final ResourceLocation[] animations;

    public AnimationState(int tickDuration, ResourceLocation... animations) {
        this.tickDuration = tickDuration;
        this.animations = animations;
    }

    /**
     * @return The amount of time in ticks the state should last for
     */
    public int getTickDuration() {
        return tickDuration;
    }

    /**
     * @return All animation resources that will play during this state
     */
    public ResourceLocation[] getAnimations() {
        return animations;
    }
}
