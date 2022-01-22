package gg.moonflower.pollen.core.client.entitlement;

import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * An entitlement that supplies a texture that must be reloaded.
 *
 * @author Ocelot
 */
@ApiStatus.Internal
public interface TexturedEntitlement {

    /**
     * @return The texture for this entitlement
     */
    @Nullable
    GeometryModelTextureTable getTexture();
}
