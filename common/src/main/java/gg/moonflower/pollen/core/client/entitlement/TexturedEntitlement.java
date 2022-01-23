package gg.moonflower.pollen.core.client.entitlement;

import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * An entitlement that supplies a texture that must be reloaded.
 *
 * @author Ocelot
 */
@ApiStatus.Internal
public interface TexturedEntitlement {

    void registerTextures(BiConsumer<ResourceLocation, GeometryModelTextureTable> textureConsumer);

    /**
     * @return The texture for this entitlement
     */
    @Nullable
    ResourceLocation getTextureKey();
}
