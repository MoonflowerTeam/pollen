package gg.moonflower.pollen.api.animation.v1;

import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import net.minecraft.util.Mth;

/**
 * Allows a single playing animation to control what render time to use when applying animations.
 *
 * @author Ocelot
 * @since 2.0.0
 */
@FunctionalInterface
public interface RenderAnimationTimer {

    /**
     * The default interpolation method.
     */
    RenderAnimationTimer LINEAR = (animation, lastTime, partialTicks) -> Mth.lerp(partialTicks, lastTime, animation.getAnimationTime());

    /**
     * Calculates the interpolated render time.
     *
     * @param animation    The animation playing
     * @param lastTime     The animation time last tick
     * @param partialTicks The percentage from last tick to this tick
     * @return The animation time to use for rendering in seconds
     */
    float getRenderAnimationTime(PlayingAnimation animation, float lastTime, float partialTicks);
}
