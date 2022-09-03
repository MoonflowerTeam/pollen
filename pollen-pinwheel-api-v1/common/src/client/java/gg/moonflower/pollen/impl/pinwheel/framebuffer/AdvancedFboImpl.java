package gg.moonflower.pollen.impl.pinwheel.framebuffer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import gg.moonflower.pollen.api.pinwheel.v1.framebuffer.AdvancedFbo;
import gg.moonflower.pollen.api.pinwheel.v1.framebuffer.AdvancedFboAttachment;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.lwjgl.opengl.GL30.*;

@ApiStatus.Internal
public class AdvancedFboImpl implements AdvancedFbo {

    private int id;
    private int width;
    private int height;
    private final AdvancedFboAttachment[] colorAttachments;
    private final AdvancedFboAttachment depthAttachment;
    private final int clearMask;

    public AdvancedFboImpl(int width, int height, AdvancedFboAttachment[] colorAttachments, @Nullable AdvancedFboAttachment depthAttachment) {
        this.id = -1;
        this.width = width;
        this.height = height;
        this.colorAttachments = colorAttachments;
        this.depthAttachment = depthAttachment;

        int mask = 0;
        if (this.hasColorAttachment(0))
            mask |= GL_COLOR_BUFFER_BIT;
        if (this.hasDepthAttachment())
            mask |= GL_DEPTH_BUFFER_BIT;
        this.clearMask = mask;
    }

    @Override
    public void create() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(this::_create);
        } else {
            this._create();
        }
    }

    private void _create() {
        for (AdvancedFboAttachment attachment : this.colorAttachments)
            attachment.create();
        if (this.depthAttachment != null)
            this.depthAttachment.create();

        this.id = glGenFramebuffers();
        this.bind(false);

        for (int i = 0; i < this.colorAttachments.length; i++)
            this.colorAttachments[i].attach(GL_FRAMEBUFFER, i);
        if (this.depthAttachment != null)
            this.depthAttachment.attach(GL_FRAMEBUFFER, 0);

        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (status != GL_FRAMEBUFFER_COMPLETE)
            throw new IllegalStateException("Advanced FBO status did not return GL_FRAMEBUFFER_COMPLETE. 0x" + Integer.toHexString(status));
        AdvancedFbo.unbind();
    }

    @Override
    public void clear() {
        if (this.clearMask != 0)
            GlStateManager._clear(this.clearMask, Minecraft.ON_OSX);
    }

    @Override
    public void bind(boolean setViewport) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this._bind(setViewport));
        } else {
            this._bind(setViewport);
        }
    }

    private void _bind(boolean setViewport) {
        glBindFramebuffer(GL_FRAMEBUFFER, this.id);
        if (setViewport)
            RenderSystem.viewport(0, 0, this.width, this.height);
    }

    @Override
    public void bindRead() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> glBindFramebuffer(GL_READ_FRAMEBUFFER, this.id));
        } else {
            glBindFramebuffer(GL_READ_FRAMEBUFFER, this.id);
        }
    }

    @Override
    public void bindDraw(boolean setViewport) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this._bindDraw(this.id, setViewport));
        } else {
            this._bindDraw(this.id, setViewport);
        }
    }

    private void _bindDraw(int id, boolean setViewport) {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id);
        if (setViewport)
            RenderSystem.viewport(0, 0, this.width, this.height);
    }

    @Override
    public void resolveToFbo(int id, int width, int height, int mask, int filtering) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this._resolveToFbo(id, width, height, mask, filtering));
        } else {
            this._resolveToFbo(id, width, height, mask, filtering);
        }
    }

    private void _resolveToFbo(int id, int width, int height, int mask, int filtering) {
        RenderSystem.assertOnRenderThreadOrInit();
        this.bindRead();
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, id);
        glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, width, height, mask, filtering);
        AdvancedFbo.unbind();
    }

    @Override
    public void resolveToScreen(int mask, int filtering) {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> this._resolveToScreen(mask, filtering));
        } else {
            this._resolveToScreen(mask, filtering);
        }
    }

    private void _resolveToScreen(int mask, int filtering) {
        RenderSystem.assertOnRenderThreadOrInit();
        Window window = Minecraft.getInstance().getWindow();
        this.bindRead();
        AdvancedFbo.unbindDraw();
        glDrawBuffer(GL_BACK);
        glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, window.getWidth(), window.getHeight(), mask, filtering);
        AdvancedFbo.unbindRead();
    }

    @Override
    public void free() {
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(this::_free);
        } else {
            this._free();
        }
    }

    private void _free() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (this.id != -1) {
            glDeleteFramebuffers(this.id);
            this.id = -1;
        }
        for (AdvancedFboAttachment attachment : this.colorAttachments)
            attachment.free();
        if (this.depthAttachment != null)
            this.depthAttachment.free();
    }

    @Override
    public int getId() {
        return id;
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
    public int getColorAttachments() {
        return colorAttachments.length;
    }

    @Override
    public boolean hasColorAttachment(int attachment) {
        return attachment >= 0 && attachment < this.colorAttachments.length;
    }

    @Override
    public boolean hasDepthAttachment() {
        return this.depthAttachment != null;
    }

    @Override
    public AdvancedFboAttachment getColorAttachment(int attachment) {
        Validate.isTrue(this.hasColorAttachment(attachment), "Color attachment " + attachment + " does not exist.");
        return this.colorAttachments[attachment];
    }

    @Override
    public AdvancedFboAttachment getDepthAttachment() {
        RenderSystem.assertOnRenderThreadOrInit();
        Validate.isTrue(this.hasDepthAttachment(), "Depth attachment does not exist.");
        return Objects.requireNonNull(this.depthAttachment);
    }

    @Override
    public Wrapper toRenderTarget() {
        return new Wrapper(this);
    }

    public static Builder copy(RenderTarget parent) {
        if (parent instanceof AdvancedFboImpl.Wrapper wrapper) {
            AdvancedFbo fbo = wrapper.fbo();
            return new AdvancedFbo.Builder(fbo.getWidth(), fbo.getHeight()).addAttachments(fbo);
        }
        return new Builder(parent.width, parent.height).addAttachments(parent);
    }

    /**
     * A vanilla {@link RenderTarget} wrapper of the {@link AdvancedFboImpl}.
     *
     * @author Ocelot
     * @see AdvancedFboImpl
     * @since 3.0.0
     */
    public static class Wrapper extends TextureTarget {
        private final AdvancedFboImpl fbo;

        private Wrapper(AdvancedFboImpl fbo) {
            super(fbo.width, fbo.height, fbo.hasDepthAttachment(), Minecraft.ON_OSX);
            this.fbo = fbo;
            this.createBuffers(this.fbo.getWidth(), this.fbo.getHeight(), Minecraft.ON_OSX);
        }

        @Override
        public void resize(int width, int height, boolean onMac) {
            if (!RenderSystem.isOnRenderThread()) {
                RenderSystem.recordRenderCall(() -> this.createBuffers(width, height, onMac));
            } else {
                this.createBuffers(width, height, onMac);
            }
        }

        @Override
        public void destroyBuffers() {
            this.fbo.close();
        }

        @Override
        public void createBuffers(int width, int height, boolean onMac) {
            this.viewWidth = width;
            this.viewHeight = height;
            if (this.fbo == null) // Assumed to be init phase so no action taken
                return;
            this.fbo.width = width;
            this.fbo.height = height;
            AdvancedFboAttachment attachment = this.fbo.hasColorAttachment(0) ? this.fbo.getColorAttachment(0) : null;
            this.width = attachment == null ? this.viewWidth : attachment.getWidth();
            this.height = attachment == null ? this.viewHeight : attachment.getHeight();
        }

        @Override
        public void setFilterMode(int framebufferFilter) {
            RenderSystem.assertOnRenderThreadOrInit();
            this.filterMode = framebufferFilter;
            for (int i = 0; i < this.fbo.getColorAttachments(); i++) {
                this.fbo.getColorAttachment(i).bindAttachment();
                GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, framebufferFilter);
                GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, framebufferFilter);
                GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
                GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
                this.fbo.getColorAttachment(i).unbindAttachment();
            }
        }

        @Override
        public void bindRead() {
            if (this.fbo.hasColorAttachment(0))
                this.fbo.getColorAttachment(0).bindAttachment();
        }

        @Override
        public void unbindRead() {
            if (this.fbo.hasColorAttachment(0))
                this.fbo.getColorAttachment(0).unbindAttachment();
        }

        @Override
        public void bindWrite(boolean setViewport) {
            this.fbo.bind(setViewport);
        }

        /**
         * @return The backing advanced fbo
         */
        public AdvancedFboImpl fbo() {
            return fbo;
        }
    }
}
