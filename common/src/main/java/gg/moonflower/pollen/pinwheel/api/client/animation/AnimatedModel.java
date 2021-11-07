package gg.moonflower.pollen.pinwheel.api.client.animation;

import gg.moonflower.pollen.pinwheel.api.common.animation.AnimationData;
import gg.moonflower.pollen.pinwheel.api.common.geometry.GeometryModelData;
import io.github.ocelot.molangcompiler.api.MolangRuntime;

/**
 * <p>Transforms model parts according to {@link AnimationData} over time.</p>
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface AnimatedModel {

    /**
     * Applies the specified animation transformations at the specified time.
     *
     * @param animationTime The time of the animation in seconds
     * @param runtime       The runtime to execute MoLang instructions in. This is generally going to be {@link MolangRuntime.Builder}
     * @param animations    The animations to play
     */
    void applyAnimations(float animationTime, MolangRuntime.Builder runtime, AnimationData... animations);

    /**
     * Fetches all locators for the specified part.
     *
     * @param part The name of the part to get locators from
     * @return All locators in the model
     */
    GeometryModelData.Locator[] getLocators(String part);
}
