package gg.moonflower.pollen.pinwheel.api.client.framebuffer.forge;

import net.minecraft.client.renderer.texture.AbstractTexture;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class AdvancedFboTextureAttachmentImpl
{
    public static void setBlurMipmap(AbstractTexture texture, boolean blur, boolean mipmap)
    {
        texture.setBlurMipmap(blur, mipmap);
    }
}
