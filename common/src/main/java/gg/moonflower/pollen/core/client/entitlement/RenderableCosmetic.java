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
}
