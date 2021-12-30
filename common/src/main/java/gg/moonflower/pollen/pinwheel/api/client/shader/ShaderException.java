package gg.moonflower.pollen.pinwheel.api.client.shader;

/**
 * Exception thrown when shaders fail to compile.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class ShaderException extends Exception {

    public ShaderException(String message) {
        super(message, null, true, true);
    }
}
