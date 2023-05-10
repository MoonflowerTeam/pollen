package gg.moonflower.pollen.api.animation.v1.state;

import net.minecraft.resources.ResourceLocation;

/**
 * An animation state is an array of animations that can play for a specific amount of time.
 * This is to allow the server to know what animation should be playing for state checking.
 *
 * @param tickDuration The amount of time in ticks the state should last for
 * @param animations   All animation resources that will play during this state
 * @author Ocelot
 * @since 2.0.0
 */
public record AnimationState(int tickDuration, ResourceLocation... animations) {

    public static final AnimationState EMPTY = new AnimationState(0);
}
