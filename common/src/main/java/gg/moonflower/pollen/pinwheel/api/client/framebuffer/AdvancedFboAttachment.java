package gg.moonflower.pollen.pinwheel.api.client.framebuffer;

import org.lwjgl.system.NativeResource;

/**
 * An attachment added to an {@link AdvancedFbo}.
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
     * @return The width of this attachment
     */
    int getWidth();

    /**
     * @return The height of this attachment
     */
    int getHeight();

    /**
     * @return The number of samples in this attachment
     */
    int getSamples();

    /**
     * @return Whether this attachment can be read from
     */
    boolean canSample();

    /**
     * A new identical attachment to this one.
     */
    AdvancedFboAttachment createCopy();
}
