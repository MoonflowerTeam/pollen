package gg.moonflower.pollen.api.pinwheel.v1.framebuffer;

import gg.moonflower.pollen.impl.pinwheel.framebuffer.AdvancedFboImpl;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.system.NativeResource;

/**
 * An attachment added to an {@link AdvancedFboImpl}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface AdvancedFboAttachment extends NativeResource {

    /**
     * Creates the attachment and initializes it with the default properties.
     */
    void create();

    /**
     * Attaches this attachment to the provided target under the specified attachment id.
     *
     * @param target     The target to attach this attachment to
     * @param attachment The attachment to attach this attachment under
     */
    void attach(int target, int attachment);

    /**
     * Binds this attachment.
     */
    void bindAttachment();

    /**
     * Unbinds this attachment.
     */
    void unbindAttachment();

    /**
     * @return The OpenGL attachment point. One of:<br><table><tr><td>{@link GL30C#GL_COLOR_ATTACHMENT0 COLOR_ATTACHMENT0}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT1 COLOR_ATTACHMENT1}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT2 COLOR_ATTACHMENT2}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT3 COLOR_ATTACHMENT3}</td></tr><tr><td>{@link GL30C#GL_COLOR_ATTACHMENT4 COLOR_ATTACHMENT4}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT5 COLOR_ATTACHMENT5}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT6 COLOR_ATTACHMENT6}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT7 COLOR_ATTACHMENT7}</td></tr><tr><td>{@link GL30C#GL_COLOR_ATTACHMENT8 COLOR_ATTACHMENT8}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT9 COLOR_ATTACHMENT9}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT10 COLOR_ATTACHMENT10}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT11 COLOR_ATTACHMENT11}</td></tr><tr><td>{@link GL30C#GL_COLOR_ATTACHMENT12 COLOR_ATTACHMENT12}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT13 COLOR_ATTACHMENT13}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT14 COLOR_ATTACHMENT14}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT15 COLOR_ATTACHMENT15}</td></tr><tr><td>{@link GL30C#GL_COLOR_ATTACHMENT16 COLOR_ATTACHMENT16}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT17 COLOR_ATTACHMENT17}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT18 COLOR_ATTACHMENT18}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT19 COLOR_ATTACHMENT19}</td></tr><tr><td>{@link GL30C#GL_COLOR_ATTACHMENT20 COLOR_ATTACHMENT20}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT21 COLOR_ATTACHMENT21}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT22 COLOR_ATTACHMENT22}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT23 COLOR_ATTACHMENT23}</td></tr><tr><td>{@link GL30C#GL_COLOR_ATTACHMENT24 COLOR_ATTACHMENT24}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT25 COLOR_ATTACHMENT25}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT26 COLOR_ATTACHMENT26}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT27 COLOR_ATTACHMENT27}</td></tr><tr><td>{@link GL30C#GL_COLOR_ATTACHMENT28 COLOR_ATTACHMENT28}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT29 COLOR_ATTACHMENT29}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT30 COLOR_ATTACHMENT30}</td><td>{@link GL30C#GL_COLOR_ATTACHMENT31 COLOR_ATTACHMENT31}</td></tr><tr><td>{@link GL30C#GL_DEPTH_ATTACHMENT DEPTH_ATTACHMENT}</td><td>{@link GL30C#GL_STENCIL_ATTACHMENT STENCIL_ATTACHMENT}</td><td>{@link GL30C#GL_DEPTH_STENCIL_ATTACHMENT DEPTH_STENCIL_ATTACHMENT}</td></tr></table>
     * @since 2.0.0
     */
    int getAttachmentType();

    /**
     * @return The OpenGL format for this attachment
     * @since 2.0.0
     */
    int getFormat();

    /**
     * @return The width of this attachment
     */
    int getWidth();

    /**
     * @return The height of this attachment
     */
    int getHeight();

    /**
     * @return Texture targets return mipmaps and render targets return samples.
     * @since 2.0.0
     */
    int getLevels();

    /**
     * @return Whether this attachment can be read from
     */
    boolean canSample();

    /**
     * A new identical attachment to this one.
     */
    AdvancedFboAttachment createCopy();
}
