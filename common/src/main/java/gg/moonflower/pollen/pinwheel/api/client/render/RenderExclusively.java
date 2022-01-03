package gg.moonflower.pollen.pinwheel.api.client.render;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An important element when defining how a {@link BlockRenderer} should interact with other renderers. When not used on a renderer, all defaults in this class will be assumed.
 *
 * @author Ocelot
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RenderExclusively {

    /**
     * Defines the render order of multiple renderers on the same block. A lower priority makes this renderer apply before others with higher priorities.
     * <p>This should preferably be used over {@link #override()} when possible to maintain compatibility with other mods
     */
    int priority() default 1000;

    /**
     * Override defines this renderer as replacing the vanilla render completely, not just adding to it.
     * <p>An override is defined as something that causes the general position or appearance of the model to greatly change.
     * <p>An example of a replacement could be causing chains to swing, allowing the main chain model to move out of the proper render area by multiple blocks. An example of when a replacement should <b>not</b> be used is removing the eye of ender from an end portal frame or adding an opening/closing animation to doors because the door would stay stationary in the vanilla spot most of the time.
     * <p>This <b>breaks</b> compatibility with any other renderer that also overrides the render, so this should be used only when necessary.
     * <p>The registered override with the highest priority will replace any other renderers that override the same block.
     * <p>All renderers that do not override the block render will be drawn <b>after</b> this renderer according to the order of {@link #priority()}
     */
    boolean override() default false;
}
