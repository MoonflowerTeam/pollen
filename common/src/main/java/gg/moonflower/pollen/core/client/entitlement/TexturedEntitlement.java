package gg.moonflower.pollen.core.client.entitlement;

import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import org.jetbrains.annotations.Nullable;

public interface TexturedEntitlement {

    @Nullable
    GeometryModelTextureTable getTexture();
}
