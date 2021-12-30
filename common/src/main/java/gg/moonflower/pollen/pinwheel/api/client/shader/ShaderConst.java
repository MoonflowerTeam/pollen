package gg.moonflower.pollen.pinwheel.api.client.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.ARBComputeShader;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43C;
import org.lwjgl.opengl.GLCapabilities;

/**
 * Constants for potentially supported shader options.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class ShaderConst {

    private static ShaderMode computeMode;
    private static ShaderMode geometryMode;

    private ShaderConst() {
    }

    @ApiStatus.Internal
    public static void init() {
        RenderSystem.assertInInitPhase();
        GLCapabilities gLCapabilities = GL.getCapabilities();

        if (gLCapabilities.OpenGL43) {
            computeMode = ShaderMode.BASE;
        } else if (gLCapabilities.GL_ARB_compute_shader) {
            computeMode = ShaderMode.ARB;
        } else {
            computeMode = ShaderMode.UNSUPPORTED;
        }

        if (gLCapabilities.OpenGL32) {
            geometryMode = ShaderMode.BASE;
        } else if (gLCapabilities.GL_ARB_geometry_shader4) {
            geometryMode = ShaderMode.ARB;
        } else if (gLCapabilities.GL_EXT_geometry_shader4) {
            geometryMode = ShaderMode.EXT;
        } else {
            geometryMode = ShaderMode.UNSUPPORTED;
        }
    }

    /**
     * If compute shaders are supported, utilizes the base or ARB compute extensions to dispatch a compute operation.
     *
     * @see GL43C#glDispatchCompute(int, int, int)
     * @see ARBComputeShader#glDispatchCompute(int, int, int)
     */
    public static void glDispatchCompute(int num_groups_x, int num_groups_y, int num_groups_z) {
        switch (computeMode) {
            default:
                throw new UnsupportedOperationException("glDispatchCompute is unsupported");
            case BASE:
                GL43C.glDispatchCompute(num_groups_x, num_groups_y, num_groups_z);
                break;
            case ARB:
                ARBComputeShader.glDispatchCompute(num_groups_x, num_groups_y, num_groups_z);
                break;
        }
    }

    /**
     * If compute shaders are supported, utilizes the base or ARB compute extensions to dispatch a compute operation.
     *
     * @see GL43C#glDispatchComputeIndirect(long)
     * @see ARBComputeShader#glDispatchComputeIndirect(long)
     */
    public static void glDispatchComputeIndirect(long indirect) {
        switch (computeMode) {
            default:
                throw new UnsupportedOperationException("glDispatchComputeIndirect is unsupported");
            case BASE:
                GL43C.glDispatchComputeIndirect(indirect);
                break;
            case ARB:
                ARBComputeShader.glDispatchComputeIndirect(indirect);
                break;
        }
    }

    /**
     * @return Whether compute shaders are supported in any capacity
     */
    public static boolean isComputeSupported() {
        return computeMode != ShaderMode.UNSUPPORTED;
    }

    /**
     * @return Whether geometry shaders are supported in any capacity
     */
    public static boolean isGeometrySupported() {
        return geometryMode != ShaderMode.UNSUPPORTED;
    }

    /**
     * <p>The current mode of GL shaders.</p>
     *
     * @author Ocelot
     */
    enum ShaderMode {
        UNSUPPORTED, BASE, ARB, EXT
    }
}
