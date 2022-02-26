package gg.moonflower.pollen.pinwheel.api.client.framebuffer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.commons.lang3.Validate;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;

/**
 * An attachment for an {@link AdvancedFbo} that represents a color texture buffer.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class AdvancedFboTextureAttachment extends AbstractTexture implements AdvancedFboAttachment {

    private final int attachmentType;
    private final int format;
    private final int width;
    private final int height;
    private final int mipmapLevels;

    public AdvancedFboTextureAttachment(int attachmentType, int format, int width, int height, int mipmapLevels) {
        this.attachmentType = attachmentType;
        this.format = format;
        this.width = width;
        this.height = height;
        this.mipmapLevels = mipmapLevels;
    }

    @ExpectPlatform
    public static void setBlurMipmap(AbstractTexture texture, boolean blur, boolean mipmap) {
        Platform.error();
    }

    private void _create() {
        RenderSystem.assertOnRenderThreadOrInit();
        this.bind();
        setBlurMipmap(this, false, this.mipmapLevels > 1);
        GlStateManager._texParameter(3553, 33085, this.mipmapLevels);
        GlStateManager._texParameter(3553, 33082, 0);
        GlStateManager._texParameter(3553, 33083, this.mipmapLevels);
        GlStateManager._texParameter(3553, 34049, 0.0F);

        for (int m = 0; m <= this.mipmapLevels; ++m) {
            GlStateManager._texImage2D(3553, m, this.format, this.width >> m, this.height >> m, 0, 6408, 5121, null);
        }
    }

    @Override
    public void create() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(this::_create);
        } else {
            this._create();
        }
    }

    @Override
    public void attach(int target, int attachment) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this._attach(target, attachment));
        } else {
            this._attach(target, attachment);
        }
    }

    private void _attach(int target, int attachment) {
        Validate.isTrue(this.attachmentType < GL_DEPTH_ATTACHMENT || attachment == 0, "Only one depth buffer attachment is supported.");
        GlStateManager._glFramebufferTexture2D(target, this.attachmentType + attachment, GL_TEXTURE_2D, this.getId(), 0); // Only draw into the first level
    }

    public int getMipmapLevels() {
        return mipmapLevels;
    }

    @Override
    public AdvancedFboTextureAttachment createCopy() {
        return new AdvancedFboTextureAttachment(this.attachmentType, this.format, this.width, this.height, this.mipmapLevels);
    }

    @Override
    public void bindAttachment() {
        this.bind();
    }

    @Override
    public void unbindAttachment() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> RenderSystem.bindTexture(0));
        } else {
            RenderSystem.bindTexture(0);
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getSamples() {
        return 1;
    }

    @Override
    public boolean canSample() {
        return true;
    }

    @Override
    public void free() {
        this.releaseId();
    }

    @Override
    public void load(ResourceManager manager) {
        this.create();
    }
}
