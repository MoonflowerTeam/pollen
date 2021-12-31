package gg.moonflower.pollen.pinwheel.api.client.shader;

import com.google.gson.JsonParser;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.JsonOps;
import gg.moonflower.pollen.api.registry.client.ShaderRegistry;
import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.registry.resource.ResourceRegistry;
import gg.moonflower.pollen.core.Pollen;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.opengl.GL20C;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import static org.lwjgl.opengl.GL20C.*;

/**
 * Loads GLSL shaders designed for rendering instead of Post-Processing.
 * <b><i>NOTE: THESE ARE NOT VANILLA SHADERS.</i></b> Use {@link ShaderRegistry} to create vanilla Minecraft {@link AdvancedShaderInstance}.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class ShaderLoader {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<ShaderPreProcessor> GLOBAL_PRE_PROCESSORS = new ArrayList<>(0);
    private static final Map<ResourceLocation, List<ShaderPreProcessor>> PRE_PROCESSORS = new HashMap<>(0);
    private static final Map<ShaderProgram.Shader, Map<ResourceLocation, Integer>> SHADERS = new HashMap<>();
    private static final Map<ResourceLocation, ShaderProgram> PROGRAMS = new HashMap<>();
    private static final Map<AdvancedShaderInstance, ResourceLocation> INSTANCES = new HashMap<>();

    private ShaderLoader() {
    }

    @ApiStatus.Internal
    public static void init() {
        ResourceRegistry.registerReloadListener(PackType.CLIENT_RESOURCES, new Reloader());
    }

    /**
     * Adds a processor to change shader information just before it is compiled into a real shader.
     *
     * @param shader    The specific shader to add the processor to
     * @param processor The processor to add
     */
    public static synchronized void addPreProcessor(ResourceLocation shader, ShaderPreProcessor processor) {
        PRE_PROCESSORS.computeIfAbsent(shader, key -> new ArrayList<>(1)).add(processor);
    }

    /**
     * Adds a processor to change shader information just before it is compiled into a real shader. Global processors are processed <b>AFTER</b> normal pre-processors.
     *
     * @param processor The processor to add
     */
    public static synchronized void addGlobalPreProcessor(ShaderPreProcessor processor) {
        GLOBAL_PRE_PROCESSORS.add(processor);
    }

    /**
     * Creates a new {@link AdvancedShaderInstance} of the specified type.
     *
     * @param program The program to create
     * @return A new shader ready to use
     */
    public static AdvancedShaderInstance create(ResourceLocation program) {
        RenderSystem.assertOnRenderThreadOrInit();
        try {
            int programId = linkShaders(program, 0);
            AdvancedShaderInstance instance = new AdvancedShaderInstance(programId);
            INSTANCES.put(instance, program);
            return instance;
        } catch (Exception e) {
            LOGGER.error("Failed to create new shader instance: " + program, e);
            AdvancedShaderInstance instance = new AdvancedShaderInstance(-1);
            INSTANCES.put(instance, program);
            return instance;
        }
    }

    private static OptionalInt getShader(ShaderProgram.Shader type, ResourceLocation id) {
        if (!SHADERS.containsKey(type))
            return OptionalInt.empty();
        Map<ResourceLocation, Integer> map = SHADERS.get(type);
        return map.containsKey(id) ? OptionalInt.of(map.get(id)) : OptionalInt.empty();
    }

    private static CharSequence preprocessShader(ResourceLocation id, String data, ShaderProgram.Shader type) {
        if (PRE_PROCESSORS.containsKey(id)) {
            for (ShaderPreProcessor processor : PRE_PROCESSORS.get(id)) {
                try {
                    data = processor.modify(id, data, type);
                } catch (Throwable t) {
                    LOGGER.error("Shader Pre-Processor threw an exception. Ignoring processing step.", t);
                }
            }
        }
        for (ShaderPreProcessor processor : GLOBAL_PRE_PROCESSORS) {
            try {
                data = processor.modify(id, data, type);
            } catch (Throwable t) {
                LOGGER.error("Shader Pre-Processor threw an exception. Ignoring processing step.", t);
            }
        }
        return data;
    }

    private static int loadShader(CharSequence data, ShaderProgram.Shader type) throws ShaderException {
        int shader = glCreateShader(type.getGLType());
        glShaderSource(shader, data);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) != GL_TRUE)
            throw new ShaderException(glGetShaderInfoLog(shader, 512));
        return shader;
    }

    private static int linkShaders(ResourceLocation program, int programId) throws ShaderException {
        if (!PROGRAMS.containsKey(program))
            throw new IllegalStateException("Unknown program: " + program);

        ShaderProgram p = PROGRAMS.get(program);
        OptionalInt vertex = p.getVertexShader().map(shader -> getShader(ShaderProgram.Shader.VERTEX, shader)).orElse(OptionalInt.empty());
        OptionalInt fragment = p.getFragmentShader().map(shader -> getShader(ShaderProgram.Shader.FRAGMENT, shader)).orElse(OptionalInt.empty());
        OptionalInt geometry = p.getGeometryShader().map(shader -> getShader(ShaderProgram.Shader.GEOMETRY, shader)).orElse(OptionalInt.empty());
        OptionalInt[] compute = p.getComputeShaders().map(array -> Stream.of(array).map(shader -> getShader(ShaderProgram.Shader.COMPUTE, shader)).toArray(OptionalInt[]::new)).orElseGet(() -> new OptionalInt[0]);

        if (compute.length > 0) {
            if (vertex.isPresent() || fragment.isPresent() || geometry.isPresent())
                throw new IllegalStateException("Compute shaders must only have compute steps");
            if (Arrays.stream(compute).anyMatch(optional -> !optional.isPresent()))
                throw new IllegalStateException("All compute shaders must be valid");
        } else {
            if (!vertex.isPresent() || !fragment.isPresent())
                throw new IllegalStateException("Both vertex and fragment shaders must be defined for a standard shader program");
        }

        if (programId > 0)
            glDeleteProgram(programId);

        programId = glCreateProgram();
        if (compute.length <= 0) {
            glAttachShader(programId, vertex.getAsInt());
            glAttachShader(programId, fragment.getAsInt());
            if (geometry.isPresent())
                glAttachShader(programId, geometry.getAsInt());
        } else {
            for (OptionalInt shader : compute)
                glAttachShader(programId, shader.orElseThrow(() -> new IllegalStateException("All compute shaders must be valid")));
        }
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) != GL_TRUE)
            throw new ShaderException(glGetProgramInfoLog(programId, 512));
        return programId;
    }

    private static class Reloader implements PollinatedPreparableReloadListener {
        @Override
        public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            CompletableFuture<Map<ShaderProgram.Shader, Map<ResourceLocation, String>>> sourcesFuture = CompletableFuture.supplyAsync(() ->
            {
                Map<ShaderProgram.Shader, Map<ResourceLocation, String>> sources = new HashMap<>();
                for (ResourceLocation location : resourceManager.listResources("shaders/program", path -> ShaderProgram.Shader.byExtension(path) != null)) {
                    ShaderProgram.Shader type = Objects.requireNonNull(ShaderProgram.Shader.byExtension(location.getPath()));
                    ResourceLocation id = new ResourceLocation(location.getNamespace(), location.getPath().substring(16, location.getPath().length() - type.getExtension().length()));
                    try (Resource resource = resourceManager.getResource(location)) {
                        sources.computeIfAbsent(type, key -> new HashMap<>()).put(id, IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        LOGGER.error("Failed to load shader: " + id, e);
                    }
                }
                return sources;
            }, backgroundExecutor);
            CompletableFuture<Map<ResourceLocation, ShaderProgram>> programsFuture = CompletableFuture.supplyAsync(() ->
            {
                Map<ResourceLocation, ShaderProgram> sources = new HashMap<>();
                for (ResourceLocation location : resourceManager.listResources("shaders/program_type", path -> path.endsWith(".json"))) {
                    ResourceLocation id = new ResourceLocation(location.getNamespace(), location.getPath().substring(21, location.getPath().length() - 5));
                    try (Resource resource = resourceManager.getResource(location)) {
                        sources.put(id, ShaderProgram.CODEC.parse(JsonOps.INSTANCE, new JsonParser().parse(new InputStreamReader(resource.getInputStream()))).getOrThrow(false, LOGGER::error));
                    } catch (Exception e) {
                        LOGGER.error("Failed to load shader program: " + id, e);
                    }
                }
                return sources;
            }, backgroundExecutor);

            return CompletableFuture.allOf(sourcesFuture, programsFuture).thenCompose(stage::wait).thenRunAsync(() ->
            {
                Map<ShaderProgram.Shader, Map<ResourceLocation, String>> sources = sourcesFuture.join();
                Map<ResourceLocation, ShaderProgram> programs = programsFuture.join();
                SHADERS.values().stream().flatMap(map -> map.values().stream()).forEach(GL20C::glDeleteShader);
                SHADERS.clear();
                PROGRAMS.clear();
                PROGRAMS.putAll(programs);

                // Load all shaders
                for (ShaderProgram.Shader type : sources.keySet()) {
                    for (Map.Entry<ResourceLocation, String> entry : sources.get(type).entrySet()) {
                        if (!type.isSupported()) {
                            LOGGER.warn(type + "");
                        }
                        try {
                            SHADERS.computeIfAbsent(type, key -> new Object2IntArrayMap<>()).put(entry.getKey(), loadShader(preprocessShader(entry.getKey(), entry.getValue(), type), type));
                        } catch (Exception e) {
                            LOGGER.error("Failed to load " + type.getDisplayName() + " Shader: " + entry.getKey(), e);
                        }
                    }
                }

                INSTANCES.keySet().removeIf(instance -> instance.getProgram() == 0); // Remove freed shaders

                // Re-link all created instances with the new shader ids
                INSTANCES.forEach((shaderInstance, program) ->
                {
                    try {
                        shaderInstance.setProgram(linkShaders(program, shaderInstance.getProgram()));
                    } catch (Exception e) {
                        shaderInstance.free();
                        shaderInstance.setProgram(-1); // -1 indicates the program should still be refreshed, but that it is not valid
                        LOGGER.error("Failed to reload shader program: " + program, e);
                    }
                });

                LOGGER.info("Loaded " + sources.values().stream().mapToInt(Map::size).sum() + " shaders and " + programs.size() + " shader programs.");
            }, task -> RenderSystem.recordRenderCall(task::run));
        }

        @Override
        public ResourceLocation getPollenId() {
            return new ResourceLocation(Pollen.MOD_ID, "shaders");
        }
    }
}
