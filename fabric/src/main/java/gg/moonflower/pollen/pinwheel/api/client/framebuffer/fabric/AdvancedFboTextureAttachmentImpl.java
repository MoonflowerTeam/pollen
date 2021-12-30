package gg.moonflower.pollen.pinwheel.api.client.framebuffer.fabric;

import net.minecraft.client.renderer.texture.AbstractTexture;

public class AdvancedFboTextureAttachmentImpl
{
    public static void setBlurMipmap(AbstractTexture texture, boolean blur, boolean mipmap)
    {
        texture.setFilter(blur, mipmap);
    }
}
