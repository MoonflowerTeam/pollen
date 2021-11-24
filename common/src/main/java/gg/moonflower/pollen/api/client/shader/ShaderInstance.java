package gg.moonflower.pollen.api.client.shader;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.NativeResource;

import java.nio.FloatBuffer;
import java.util.Map;
import java.util.OptionalInt;

import static org.lwjgl.opengl.GL20C.*;

/**
 * A usable instance of a {@link ShaderProgram}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class ShaderInstance implements NativeResource {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final FloatBuffer MATRIX_4_4 = BufferUtils.createFloatBuffer(4 * 4);

    private final Map<CharSequence, Integer> uniforms;
    private int program;

    ShaderInstance(int program) {
        this.program = program;
        this.uniforms = new Object2IntArrayMap<>();
    }

    /**
     * Checks for a uniform with the specified name.
     *
     * @param uniformName The name of the uniform to fetch
     * @return An optional of the uniform with that name
     */
    public OptionalInt getUniform(CharSequence uniformName) {
        int uniform = this.uniforms.computeIfAbsent(uniformName, key ->
        {
            int location = glGetUniformLocation(this.program, uniformName);
            if (location == -1)
                LOGGER.warn("Unknown uniform: " + uniformName);
            return location;
        });
        return uniform == -1 ? OptionalInt.empty() : OptionalInt.of(uniform);
    }

    /**
     * Loads booleans into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload
     */
    public void loadBoolean(CharSequence uniformName, boolean... values) {
        int[] integers = new int[values.length];
        for (int i = 0; i < values.length; i++)
            integers[i] = values[i] ? 1 : 0;
        this.loadInt(uniformName, integers);
    }

    /**
     * Loads 2D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload. Must be a multiple of 2
     */
    public void loadVector2(CharSequence uniformName, boolean... values) {
        int[] integers = new int[values.length];
        for (int i = 0; i < values.length; i++)
            integers[i] = values[i] ? 1 : 0;
        this.loadVector2(uniformName, integers);
    }

    /**
     * Loads 3D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload. Must be a multiple of 3
     */
    public void loadVector3(CharSequence uniformName, boolean... values) {
        int[] integers = new int[values.length];
        for (int i = 0; i < values.length; i++)
            integers[i] = values[i] ? 1 : 0;
        this.loadVector3(uniformName, integers);
    }

    /**
     * Loads 4D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload. Must be a multiple of 4
     */
    public void loadVector4(CharSequence uniformName, boolean... values) {
        int[] integers = new int[values.length];
        for (int i = 0; i < values.length; i++)
            integers[i] = values[i] ? 1 : 0;
        this.loadVector4(uniformName, integers);
    }

    /**
     * Loads integers into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload
     */
    public void loadInt(CharSequence uniformName, int... values) {
        if (values.length == 0)
            return;
        this.getUniform(uniformName).ifPresent(uniform -> glUniform1iv(uniform, values));
    }

    /**
     * Loads 2D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload. Must be a multiple of 2
     */
    public void loadVector2(CharSequence uniformName, int... values) {
        if (values.length == 0)
            return;
        Validate.isTrue(values.length % 2 == 0);
        this.getUniform(uniformName).ifPresent(uniform -> glUniform2iv(uniform, values));
    }

    /**
     * Loads 3D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload. Must be a multiple of 3
     */
    public void loadVector3(CharSequence uniformName, int... values) {
        if (values.length == 0)
            return;
        Validate.isTrue(values.length % 3 == 0);
        this.getUniform(uniformName).ifPresent(uniform -> glUniform3iv(uniform, values));
    }

    /**
     * Loads 4D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload. Must be a multiple of 4
     */
    public void loadVector4(CharSequence uniformName, int... values) {
        if (values.length == 0)
            return;
        Validate.isTrue(values.length % 4 == 0);
        this.getUniform(uniformName).ifPresent(uniform -> glUniform4iv(uniform, values));
    }

    /**
     * Loads floats into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload
     */
    public void loadFloat(CharSequence uniformName, float... values) {
        if (values.length == 0)
            return;
        this.getUniform(uniformName).ifPresent(uniform -> glUniform1fv(uniform, values));
    }

    /**
     * Loads 2D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload. Must be a multiple of 2
     */
    public void loadVector2(CharSequence uniformName, float... values) {
        if (values.length == 0)
            return;
        Validate.isTrue(values.length % 2 == 0);
        this.getUniform(uniformName).ifPresent(uniform -> glUniform2fv(uniform, values));
    }

    /**
     * Loads 3D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload. Must be a multiple of 3
     */
    public void loadVector3(CharSequence uniformName, float... values) {
        if (values.length == 0)
            return;
        Validate.isTrue(values.length % 3 == 0);
        this.getUniform(uniformName).ifPresent(uniform -> glUniform3fv(uniform, values));
    }

    /**
     * Loads 4D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The values to upload. Must be a multiple of 4
     */
    public void loadVector4(CharSequence uniformName, float... values) {
        if (values.length == 0)
            return;
        Validate.isTrue(values.length % 4 == 0);
        this.getUniform(uniformName).ifPresent(uniform -> glUniform4fv(uniform, values));
    }

    /**
     * Loads 3D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The vector value to upload
     */
    public void loadVector(CharSequence uniformName, Vector3f... values) {
        float[] floats = new float[values.length * 3];
        for (int i = 0; i < values.length; i++) {
            Vector3f value = values[i];
            floats[i * 3] = value.x();
            floats[i * 3 + 1] = value.y();
            floats[i * 3 + 2] = value.z();
        }
        this.loadVector3(uniformName, floats);
    }

    /**
     * Loads 4D vectors into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param values      The vector value to upload
     */
    public void loadVector(CharSequence uniformName, Vector4f... values) {
        float[] floats = new float[values.length * 4];
        for (int i = 0; i < values.length; i++) {
            Vector4f value = values[i];
            floats[i * 4] = value.x();
            floats[i * 4 + 1] = value.y();
            floats[i * 4 + 2] = value.z();
            floats[i * 4 + 3] = value.w();
        }
        this.loadVector4(uniformName, floats);
    }

    /**
     * Loads a 4x4 matrix into the shader.
     *
     * @param uniformName The name of the uniform to load
     * @param matrix      The matrix data to upload
     */
    public void loadMatrix(CharSequence uniformName, Matrix4f matrix) {
        this.getUniform(uniformName).ifPresent(uniform ->
        {
            matrix.store(MATRIX_4_4);
            glUniformMatrix4fv(uniform, false, MATRIX_4_4);
        });
    }

    /**
     * Binds this shader for using with future render calls.
     */
    public void bind() {
        if (this.program <= 0)
            return;
        glUseProgram(this.program);
    }

    /**
     * Unbinds the current shader and sets it back to the compatibility pipeline.
     */
    public static void unbind() {
        glUseProgram(0);
    }

    @Override
    public void free() {
        if (this.program == 0)
            return;
        if (this.program > 0)
            glDeleteProgram(this.program);
        this.setProgram(0);
    }

    void setProgram(int program) {
        this.program = program;
        this.uniforms.clear();
    }

    /**
     * @return The OpenGL id of the program
     */
    public int getProgram() {
        return program;
    }
}
