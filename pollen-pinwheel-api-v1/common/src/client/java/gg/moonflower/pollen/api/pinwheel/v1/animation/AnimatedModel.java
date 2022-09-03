package gg.moonflower.pollen.api.pinwheel.v1.animation;

import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryModelData;
import io.github.ocelot.molangcompiler.api.MolangRuntime;

/**
 * Transforms model parts according to {@link AnimationData} over time.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface AnimatedModel {

    /**
     * Calculates the length of an animation based on the current time and loop modes of all animations.
     *
     * @param animationTime The current time in seconds
     * @param animations    The animations to get the length of
     * @return The length of the set of animations
     */
    static float getAnimationLength(float animationTime, AnimationData... animations) {
        boolean loop = false;
        float length = 0;
        for (AnimationData animation : animations) {
            if (animation.loop() == AnimationData.Loop.LOOP)
                loop = true;
            if (animation.animationLength() > length)
                length = animation.animationLength();
        }

        if (loop && animationTime > length)
            return length;
        return Integer.MAX_VALUE;
    }

    /**
     * Applies the specified animation transformations at the specified time.
     *
     * @param animationTime The time of the animation in seconds
     * @param runtime       The runtime to execute MoLang instructions in. This is generally going to be {@link MolangRuntime.Builder}
     * @param weights       Defined weights for each animation. This is multiplied after the animation is resolved
     * @param animations    The animations to play
     */
    void applyAnimations(float animationTime, MolangRuntime.Builder runtime, float[] weights, AnimationData... animations);

    /**
     * Applies the specified animation transformations at the specified time.
     *
     * @param animationTime The time of the animation in seconds
     * @param runtime       The runtime to execute MoLang instructions in. This is generally going to be {@link MolangRuntime.Builder}
     * @param animations    The animations to play
     */
    default void applyAnimations(float animationTime, MolangRuntime.Builder runtime, AnimationData... animations) {
        this.applyAnimations(animationTime, runtime, new float[0], animations);
    }

    /**
     * Fetches all locators for the specified part.
     *
     * @param part The name of the part to get locators from
     * @return All locators in the model
     */
    GeometryModelData.Locator[] getLocators(String part);
}
