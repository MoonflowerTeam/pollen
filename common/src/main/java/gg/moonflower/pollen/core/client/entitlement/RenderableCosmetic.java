package gg.moonflower.pollen.core.client.entitlement;

import org.jetbrains.annotations.ApiStatus;

/**
 * An entitlement that can be rendered on the cosmetic layer.
 */
@ApiStatus.Internal
public interface RenderableCosmetic extends ModelEntitlement, TexturedEntitlement {

    /**
     * @return Whether to display this cosmetic
     */
    boolean isEnabled();

    /**
     * @return The red factor to use when rendering
     */
    default float getRed() {
        return 1.0F;
    }

    /**
     * @return The green factor to use when rendering
     */
    default float getGreen() {
        return 1.0F;
    }

    /**
     * @return The bluue factor to use when rendering
     */
    default float getBlue() {
        return 1.0F;
    }

    /**
     * @return The alpha factor to use when rendering
     */
    default float getAlpha() {
        return 1.0F;
    }
}
