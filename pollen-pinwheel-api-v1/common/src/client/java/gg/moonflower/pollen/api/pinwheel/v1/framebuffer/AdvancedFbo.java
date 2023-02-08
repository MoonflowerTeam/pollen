package gg.moonflower.pollen.api.pinwheel.v1.framebuffer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import gg.moonflower.pollen.impl.pinwheel.framebuffer.AdvancedFboImpl;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.Validate;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.NativeResource;

import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

/**
 * A framebuffer that has more capabilities than the vanilla Minecraft {@link RenderTarget}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface AdvancedFbo extends NativeResource {

    /**
     * Creates the framebuffer and all attachments.
     */
    void create();

    /**
     * Clears the buffers in this framebuffer.
     */
    void clear();

    /**
     * Binds this framebuffer for read and draw requests.
     *
     * @param setViewport Whether to set the viewport to fit the bounds of this framebuffer
     */
    void bind(boolean setViewport);

    /**
     * Binds this framebuffer for read requests.
     */
    void bindRead();

    /**
     * Binds this framebuffer for draw requests.
     *
     * @param setViewport Whether to set the viewport to fit the bounds of this framebuffer
     */
    void bindDraw(boolean setViewport);

    /**
     * Binds the main Minecraft framebuffer for writing and reading.
     */
    static void unbind() {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        if (mainTarget != null) {
            mainTarget.bindWrite(true);
            return;
        }

        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> glBindFramebuffer(GL_FRAMEBUFFER, 0));
        } else {
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        }
    }

    /**
     * Binds the main Minecraft framebuffer for reading.
     */
    static void unbindRead() {
        int mainTarget = Minecraft.getInstance().getMainRenderTarget() != null ? Minecraft.getInstance().getMainRenderTarget().frameBufferId : 0;
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> glBindFramebuffer(GL_READ_FRAMEBUFFER, mainTarget));
        } else {
            glBindFramebuffer(GL_READ_FRAMEBUFFER, mainTarget);
        }
    }

    /**
     * Binds the main Minecraft framebuffer for drawing.
     */
    static void unbindDraw() {
        int mainTarget = Minecraft.getInstance().getMainRenderTarget() != null ? Minecraft.getInstance().getMainRenderTarget().frameBufferId : 0;
        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> glBindFramebuffer(GL_DRAW_FRAMEBUFFER, mainTarget));
        } else {
            glBindFramebuffer(GL_DRAW_FRAMEBUFFER, mainTarget);
        }
    }

    /**
     * Resolves this framebuffer to the framebuffer with the specified id as the target.
     *
     * @param id        The id of the framebuffer to copy into
     * @param width     The width of the framebuffer being copied into
     * @param height    The height of the framebuffer being copied into
     * @param mask      The buffers to copy into the provided framebuffer
     * @param filtering The filter to use if this framebuffer and the provided framebuffer are different sizes
     */
    void resolveToFbo(int id, int width, int height, int mask, int filtering);

    /**
     * Resolves this framebuffer to the provided advanced framebuffer as the target.
     *
     * @param target The target framebuffer to copy data into
     */
    default void resolveToAdvancedFbo(AdvancedFbo target) {
        this.resolveToFbo(target.getId(), target.getWidth(), target.getHeight(), GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
    }

    /**
     * Resolves this framebuffer to the provided advanced framebuffer as the target.
     *
     * @param target    The target framebuffer to copy data into
     * @param mask      The buffers to copy into the provided framebuffer
     * @param filtering The filter to use if this framebuffer and the provided framebuffer are different sizes
     */
    default void resolveToAdvancedFbo(AdvancedFbo target, int mask, int filtering) {
        this.resolveToFbo(target.getId(), target.getWidth(), target.getHeight(), mask, filtering);
    }

    /**
     * Resolves this framebuffer to the provided minecraft framebuffer as the target.
     *
     * @param target The target framebuffer to copy data into
     */
    default void resolveToFramebuffer(RenderTarget target) {
        this.resolveToFbo(target.frameBufferId, target.viewWidth, target.viewHeight, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
    }

    /**
     * Resolves this framebuffer to the provided minecraft framebuffer as the target.
     *
     * @param target    The target framebuffer to copy data into
     * @param mask      The buffers to copy into the provided framebuffer
     * @param filtering The filter to use if this framebuffer and the provided framebuffer are different sizes
     */
    default void resolveToFramebuffer(RenderTarget target, int mask, int filtering) {
        this.resolveToFbo(target.frameBufferId, target.viewWidth, target.viewHeight, mask, filtering);
    }

    /**
     * Resolves this framebuffer to the window framebuffer as the target.
     */
    default void resolveToScreen() {
        this.resolveToScreen(GL_COLOR_BUFFER_BIT, GL_NEAREST);
    }

    /**
     * Resolves this framebuffer to the window framebuffer as the target.
     *
     * @param mask      The buffers to copy into the provided framebuffer
     * @param filtering The filter to use if this framebuffer and the provided framebuffer are different sizes
     */
    void resolveToScreen(int mask, int filtering);

    /**
     * @return The id of this framebuffer or -1 if it has been deleted
     */
    int getId();

    /**
     * @return The width of this framebuffer
     */
    int getWidth();

    /**
     * @return The height of this framebuffer
     */
    int getHeight();

    /**
     * @return The amount of color attachments in this framebuffer
     */
    int getColorAttachments();

    /**
     * Checks to see if the provided attachment has been added to this framebuffer.
     *
     * @param attachment The attachment to check
     * @return Whether there is a valid attachment in the specified slot
     */
    boolean hasColorAttachment(int attachment);

    /**
     * @return Whether there is a depth attachment added to this framebuffer
     */
    boolean hasDepthAttachment();

    /**
     * Checks the attachments for the specified slot. If the amount of attachments is unknown, use {@link #hasColorAttachment(int)} to verify before calling this.
     *
     * @param attachment The attachment to get
     * @return The attachment in the specified attachment slot
     * @throws IllegalArgumentException If there is no attachment in the specified attachment slot
     */
    AdvancedFboAttachment getColorAttachment(int attachment);

    /**
     * Checks to see if the provided attachment has been added to this framebuffer and is a texture attachment.
     *
     * @param attachment The attachment to check
     * @return Whether there is a valid attachment in the specified slot
     */
    default boolean isColorTextureAttachment(int attachment) {
        return this.hasColorAttachment(attachment) && this.getColorAttachment(attachment) instanceof AdvancedFboTextureAttachment;
    }

    /**
     * Checks to see if the provided attachment has been added to this framebuffer and is a render attachment.
     *
     * @param attachment The attachment to check
     * @return Whether there is a valid attachment in the specified slot
     */
    default boolean isColorRenderAttachment(int attachment) {
        return this.hasColorAttachment(attachment) && this.getColorAttachment(attachment) instanceof AdvancedFboRenderAttachment;
    }

    /**
     * Checks the attachments for the specified slot. If the attachment is not known to be an {@link AdvancedFboTextureAttachment}, use {@link #isColorTextureAttachment(int)} before calling this.
     *
     * @param attachment The attachment to get
     * @return The texture attachment in the specified attachment slot
     * @throws IllegalArgumentException If there is no attachment in the specified attachment slot, or it is not an {@link AdvancedFboTextureAttachment}
     */
    default AdvancedFboTextureAttachment getColorTextureAttachment(int attachment) {
        AdvancedFboAttachment advancedFboAttachment = this.getColorAttachment(attachment);
        Validate.isTrue(this.isColorTextureAttachment(attachment), "Color attachment " + attachment + " must be a texture attachment to modify texture information.");
        return (AdvancedFboTextureAttachment) advancedFboAttachment;
    }

    /**
     * Checks the attachments for the specified slot. If the attachment is not known to be an {@link AdvancedFboRenderAttachment}, use {@link #isColorRenderAttachment(int)} before calling this.
     *
     * @param attachment The attachment to get
     * @return The render attachment in the specified attachment slot
     * @throws IllegalArgumentException If there is no attachment in the specified attachment slot, or it is not an {@link AdvancedFboRenderAttachment}
     */
    default AdvancedFboRenderAttachment getColorRenderAttachment(int attachment) {
        AdvancedFboAttachment advancedFboAttachment = this.getColorAttachment(attachment);
        Validate.isTrue(this.isColorRenderAttachment(attachment), "Color attachment " + attachment + " must be a render attachment to modify render information.");
        return (AdvancedFboRenderAttachment) advancedFboAttachment;
    }

    /**
     * @return The depth attachment of this framebuffer
     * @throws IllegalArgumentException If there is no depth attachment in this framebuffer
     */
    AdvancedFboAttachment getDepthAttachment();

    /**
     * @return Whether a depth texture attachment has been added to this framebuffer
     */
    default boolean isDepthTextureAttachment() {
        return this.hasDepthAttachment() && this.getDepthAttachment() instanceof AdvancedFboTextureAttachment;
    }

    /**
     * @return Whether a depth render attachment has been added to this framebuffer
     */
    default boolean isDepthRenderAttachment() {
        return this.hasDepthAttachment() && this.getDepthAttachment() instanceof AdvancedFboRenderAttachment;
    }

    /**
     * Checks this framebuffer for a depth buffer texture attachment. If the attachment is not known to be a {@link AdvancedFboTextureAttachment}, use {@link #isDepthTextureAttachment()} before calling this.
     *
     * @return The texture attachment in the specified attachment slot
     * @throws IllegalArgumentException If there is no depth attachment in this framebuffer, or it is not an {@link AdvancedFboTextureAttachment}
     */
    default AdvancedFboTextureAttachment getDepthTextureAttachment() {
        AdvancedFboAttachment advancedFboAttachment = this.getDepthAttachment();
        Validate.isTrue(this.isDepthTextureAttachment(), "Depth attachment must be a texture attachment to modify texture information.");
        return (AdvancedFboTextureAttachment) advancedFboAttachment;
    }

    /**
     * Checks this framebuffer for a depth buffer render attachment. If the attachment is not known to be a {@link AdvancedFboRenderAttachment}, use {@link #isDepthRenderAttachment()} before calling this.
     *
     * @return The render attachment in the specified attachment slot
     * @throws IllegalArgumentException If there is no depth attachment in this framebuffer, or it is not an {@link AdvancedFboRenderAttachment}
     */
    default AdvancedFboRenderAttachment getDepthRenderAttachment() {
        AdvancedFboAttachment advancedFboAttachment = this.getDepthAttachment();
        Validate.isTrue(this.isDepthRenderAttachment(), "Depth attachment must be a render attachment to modify render information.");
        return (AdvancedFboRenderAttachment) advancedFboAttachment;
    }

    /**
     * @return A {@link RenderTarget} that uses this advanced fbo as the target
     */
    RenderTarget toRenderTarget();

    /**
     * Creates a new {@link AdvancedFbo} with the provided width and height.
     *
     * @param width  The width of the canvas
     * @param height The height of the canvas
     * @return A builder to construct a new FBO
     */
    static Builder withSize(int width, int height) {
        return new Builder(width, height);
    }

    /**
     * Creates a copy of the provided {@link AdvancedFbo}.
     *
     * @param parent The parent to copy attachments from
     * @return A builder to construct a new FBO
     */
    static Builder copy(AdvancedFbo parent) {
        return new Builder(parent.getWidth(), parent.getHeight()).addAttachments(parent);
    }

    /**
     * Creates a copy of the provided {@link RenderTarget}.
     *
     * @param parent The parent to copy attachments from
     * @return A builder to construct a new FBO
     */
    static Builder copy(RenderTarget parent) {
        return AdvancedFboImpl.copy(parent);
    }

    /**
     * A builder used to attach buffers to an {@link AdvancedFbo}.
     *
     * @author Ocelot
     * @see AdvancedFbo
     * @since 2.4.0
     */
    class Builder {

        private static final int MAX_COLOR_ATTACHMENTS = glGetInteger(GL_MAX_COLOR_ATTACHMENTS);

        private final int width;
        private final int height;
        private final List<AdvancedFboAttachment> colorAttachments;
        private AdvancedFboAttachment depthAttachment;
        private int mipmaps;
        private int samples;
        private int format;

        public Builder(int width, int height) {
            this.width = width;
            this.height = height;
            this.colorAttachments = new LinkedList<>();
            this.depthAttachment = null;
            this.mipmaps = 0;
            this.samples = 1;
            this.format = GL_RGBA;
        }

        private void validateColorSize() {
            Validate.inclusiveBetween(0, MAX_COLOR_ATTACHMENTS, this.colorAttachments.size());
        }

        /**
         * Adds copies of the buffers inside the specified fbo.
         *
         * @param parent The parent to add the attachments for
         */
        public Builder addAttachments(AdvancedFbo parent) {
            for (int i = 0; i < parent.getColorAttachments(); i++)
                this.colorAttachments.add(parent.getColorAttachment(i).createCopy());
            this.validateColorSize();
            if (parent.hasDepthAttachment()) {
                Validate.isTrue(this.depthAttachment == null, "Only one depth attachment can be applied to an FBO.");
                this.depthAttachment = parent.getDepthAttachment().createCopy();
            }
            return this;
        }

        /**
         * Adds copies of the buffers inside the specified fbo.
         *
         * @param parent The parent to add the attachments for
         */
        public Builder addAttachments(RenderTarget parent) {
            this.setMipmaps(0);
            this.addColorTextureBuffer(parent.width, parent.height);
            if (parent.useDepth) {
                Validate.isTrue(this.depthAttachment == null, "Only one depth attachment can be applied to an FBO.");
                this.setSamples(1);
                this.setDepthRenderBuffer(parent.width, parent.height);
            }
            return this;
        }

        /**
         * Sets the number of mipmaps levels to use for texture attachments. <code>0</code> is the default for none.
         *
         * @param mipmaps The levels to have
         */
        public Builder setMipmaps(int mipmaps) {
            this.mipmaps = mipmaps;
            return this;
        }

        /**
         * Sets the number of samples to use for render buffer attachments. <code>1</code> is the default for single sample buffers.
         *
         * @param samples The samples to have
         */
        public Builder setSamples(int samples) {
            this.samples = samples;
            return this;
        }

        /**
         * Sets the format to use for texture attachments. {@link GL11#GL_RGBA} is the default.
         *
         * @param format The new format to use
         */
        public Builder setFormat(int format) {
            this.format = format;
            return this;
        }

        /**
         * Adds a color texture buffer with the size of the framebuffer and 1 mipmap level.
         */
        public Builder addColorTextureBuffer() {
            this.addColorTextureBuffer(this.width, this.height);
            return this;
        }

        /**
         * Adds a color texture buffer with the specified size and the specified mipmap levels.
         *
         * @param width  The width of the texture buffer
         * @param height The height of the texture buffer
         */
        public Builder addColorTextureBuffer(int width, int height) {
            this.colorAttachments.add(new AdvancedFboTextureAttachment(GL_COLOR_ATTACHMENT0, this.format, width, height, this.mipmaps));
            this.validateColorSize();
            return this;
        }

        /**
         * <p>Adds a color render buffer with the size of the framebuffer and 1 sample.</p>
         * <p><b><i>NOTE: COLOR RENDER BUFFERS CAN ONLY BE COPIED TO OTHER FRAMEBUFFERS</i></b></p>
         */
        public Builder addColorRenderBuffer() {
            this.addColorRenderBuffer(this.width, this.height);
            return this;
        }

        /**
         * <p>Adds a color render buffer with the specified size and the specified samples.</p>
         * <p><b><i>NOTE: COLOR RENDER BUFFERS CAN ONLY BE COPIED TO OTHER FRAMEBUFFERS</i></b></p>
         *
         * @param width  The width of the render buffer
         * @param height The height of the render buffer
         */
        public Builder addColorRenderBuffer(int width, int height) {
            this.colorAttachments.add(new AdvancedFboRenderAttachment(GL_COLOR_ATTACHMENT0, this.format, width, height, this.samples));
            this.validateColorSize();
            return this;
        }

        /**
         * Sets the depth texture buffer to the size of the framebuffer and 1 mipmap level.
         */
        public Builder setDepthTextureBuffer() {
            this.setDepthTextureBuffer(this.width, this.height);
            return this;
        }

        /**
         * Sets the depth texture buffer to the size of the framebuffer and the specified mipmap levels.
         *
         * @param width  The width of the texture buffer
         * @param height The height of the texture buffer
         */
        public Builder setDepthTextureBuffer(int width, int height) {
            Validate.isTrue(this.depthAttachment == null, "Only one depth attachment can be applied to an FBO.");
            this.depthAttachment = new AdvancedFboTextureAttachment(GL_DEPTH_ATTACHMENT, this.format, width, height, this.mipmaps);
            return this;
        }

        /**
         * <p>Sets the depth texture buffer to the size of the framebuffer and 1 sample.</p>
         * <p><b><i>NOTE: DEPTH RENDER BUFFERS CAN ONLY BE COPIED TO OTHER FRAMEBUFFERS</i></b></p>
         */
        public Builder setDepthRenderBuffer() {
            this.setDepthRenderBuffer(this.width, this.height);
            return this;
        }

        /**
         * <p>Sets the depth texture buffer to the specified size and the specified samples.</p>
         * <p><b><i>NOTE: DEPTH RENDER BUFFERS CAN ONLY BE COPIED TO OTHER FRAMEBUFFERS</i></b></p>
         *
         * @param width  The width of the render buffer
         * @param height The height of the render buffer
         */
        public Builder setDepthRenderBuffer(int width, int height) {
            Validate.isTrue(this.depthAttachment == null, "Only one depth attachment can be applied to an FBO.");
            this.depthAttachment = new AdvancedFboRenderAttachment(GL_DEPTH_ATTACHMENT, GL_DEPTH_COMPONENT24, width, height, this.samples);
            return this;
        }

        /**
         * Constructs a new {@link AdvancedFbo} with the specified attachments.
         *
         * @param create Whether to immediately create the buffer
         * @return A new {@link AdvancedFbo} with the specified builder properties.
         */
        public AdvancedFbo build(boolean create) {
            if (this.colorAttachments.isEmpty())
                throw new IllegalArgumentException("Framebuffer needs at least one color attachment to be complete.");
            int samples = -1;
            for (AdvancedFboAttachment attachment : this.colorAttachments) {
                if (samples == -1) {
                    samples = attachment instanceof AdvancedFboRenderAttachment renderAttachment ? renderAttachment.getLevels() : 1;
                    continue;
                }
                if (attachment instanceof AdvancedFboRenderAttachment && attachment.getLevels() != samples)
                    throw new IllegalArgumentException("Framebuffer attachments need to have the same number of samples to be complete.");
            }
            if (this.depthAttachment != null && this.depthAttachment.getLevels() != samples)
                throw new IllegalArgumentException("Framebuffer attachments need to have the same number of samples to be complete.");
            AdvancedFbo advancedFbo = new AdvancedFboImpl(this.width, this.height, this.colorAttachments.toArray(new AdvancedFboAttachment[0]), this.depthAttachment);
            if (create)
                advancedFbo.create();
            return advancedFbo;
        }
    }
}
