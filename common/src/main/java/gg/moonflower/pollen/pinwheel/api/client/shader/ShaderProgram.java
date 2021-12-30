package gg.moonflower.pollen.pinwheel.api.client.shader;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

import static org.lwjgl.opengl.GL20C.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20C.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32C.GL_GEOMETRY_SHADER;
import static org.lwjgl.opengl.GL43C.GL_COMPUTE_SHADER;

/**
 * A template for creating a {@link ShaderInstance}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class ShaderProgram {
    public static final Codec<ShaderProgram> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.optionalFieldOf("vertex").forGetter(ShaderProgram::getVertexShader),
            ResourceLocation.CODEC.optionalFieldOf("fragment").forGetter(ShaderProgram::getFragmentShader),
            ResourceLocation.CODEC.optionalFieldOf("geometry").forGetter(ShaderProgram::getGeometryShader),
            ResourceLocation.CODEC.listOf().optionalFieldOf("compute").xmap(array -> array.map(list -> list.toArray(new ResourceLocation[0])), list -> list.map(Arrays::asList)).forGetter(ShaderProgram::getComputeShaders)
    ).apply(instance, (vertex, fragment, geometry, computeShaders) -> new ShaderProgram(vertex.orElse(null), fragment.orElse(null), geometry.orElse(null), computeShaders.orElseGet(() -> new ResourceLocation[0]))));

    private final ResourceLocation vertexShader;
    private final ResourceLocation fragmentShader;
    private final ResourceLocation geometryShader;
    private final ResourceLocation[] computeShaders;

    public ShaderProgram(@Nullable ResourceLocation vertexShader, @Nullable ResourceLocation fragmentShader, @Nullable ResourceLocation geometryShader, ResourceLocation[] computeShaders) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
        this.geometryShader = geometryShader;
        this.computeShaders = computeShaders;
    }

    public Optional<ResourceLocation> getVertexShader() {
        return Optional.ofNullable(this.vertexShader);
    }

    public Optional<ResourceLocation> getFragmentShader() {
        return Optional.ofNullable(this.fragmentShader);
    }

    public Optional<ResourceLocation> getGeometryShader() {
        return Optional.ofNullable(this.geometryShader);
    }

    public Optional<ResourceLocation[]> getComputeShaders() {
        return this.computeShaders.length == 0 ? Optional.empty() : Optional.of(this.computeShaders);
    }

    /**
     * <p>Types of shader supported by the shader pipeline.</p>
     *
     * @author Ocelot
     */
    public enum Shader {
        VERTEX("Vertex", ".vert", GL_VERTEX_SHADER),
        FRAGMENT("Fragment", ".frag", GL_FRAGMENT_SHADER),
        GEOMETRY("Geometry", ".geom", GL_GEOMETRY_SHADER, ShaderConst.isGeometrySupported()),
        COMPUTE("Compute", ".comp", GL_COMPUTE_SHADER, ShaderConst.isComputeSupported());

        private final String displayName;
        private final String extension;
        private final int type;
        private final boolean supported;

        Shader(String displayName, String extension, int type, boolean supported) {
            this.displayName = displayName;
            this.extension = extension;
            this.type = type;
            this.supported = supported;
        }

        Shader(String displayName, String extension, int type) {
            this(displayName, extension, type, true);
        }

        /**
         * Retrieves a shader type by the specified extension.
         *
         * @param fileName The name of the file
         * @return The type of shader based on extension
         */
        @Nullable
        public static ShaderProgram.Shader byExtension(String fileName) {
            for (Shader type : values())
                if (fileName.endsWith(type.extension))
                    return type;
            return null;
        }

        /**
         * @return The visible name of this shader type
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * @return The file name extension of this shader type
         */
        public String getExtension() {
            return extension;
        }

        /**
         * @return The OpenGL enum type
         */
        public int getGLType() {
            return type;
        }

        /**
         * @return Whether this shader type is supported
         */
        public boolean isSupported() {
            return supported;
        }
    }
}
