package gg.moonflower.pollen.pinwheel.api.common.particle.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import gg.moonflower.pollen.api.util.JSONTupleParser;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.common.particle.Flipbook;
import gg.moonflower.pollen.pinwheel.api.common.particle.ParticleParser;
import gg.moonflower.pollen.pinwheel.api.common.particle.render.SingleQuadRenderProperties;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.client.Camera;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Component that specifies the billboard properties of a particle.
 *
 * @author Ocelot
 * @since 1.6.0
 */
public class ParticleAppearanceBillboardComponent implements CustomParticleComponent, CustomParticleListener, CustomParticleRenderComponent {

    private static final UVSetter DEFAULT = (__, properties) -> properties.setUV(0, 0, 1, 1);

    private final MolangExpression[] size;
    private final FaceCameraMode cameraMode;
    private final float minSpeedThreshold;
    private final MolangExpression[] customDirection;
    private final UVSetter uvSetter;

    public ParticleAppearanceBillboardComponent(JsonElement json) {
        JsonObject jsonObject = json.getAsJsonObject();
        this.size = JSONTupleParser.getExpression(jsonObject, "size", 2, null);
        this.cameraMode = parseCameraMode(GsonHelper.getAsString(jsonObject, "facing_camera_mode"));

        if (jsonObject.has("direction")) {
            JsonObject directionJson = GsonHelper.getAsJsonObject(jsonObject, "direction");
            if ("custom_direction".equals(GsonHelper.getAsString(directionJson, "mode"))) {
                this.minSpeedThreshold = 0.01F;
                this.customDirection = JSONTupleParser.getExpression(directionJson, "direction", 3, () -> new MolangExpression[]{MolangExpression.ZERO, MolangExpression.ZERO, MolangExpression.ZERO});
            } else {
                this.minSpeedThreshold = GsonHelper.getAsFloat(directionJson, "min_speed_threshold", 0.01F);
                this.customDirection = null;
            }
        } else {
            this.minSpeedThreshold = 0.01F;
            this.customDirection = null;
        }

        if (jsonObject.has("uv")) {
            JsonObject uvJson = GsonHelper.getAsJsonObject(jsonObject, "uv");
            int textureWidth = GsonHelper.getAsInt(uvJson, "texture_width", 1);
            int textureHeight = GsonHelper.getAsInt(uvJson, "texture_height", 1);

            if (uvJson.has("flipbook")) {
                this.uvSetter = UVSetter.flipbook(textureWidth, textureHeight, ParticleParser.parseFlipbook(uvJson.get("flipbook")));
            } else {
                MolangExpression[] uv = JSONTupleParser.getExpression(uvJson, "uv", 2, null);
                MolangExpression[] uvSize = JSONTupleParser.getExpression(uvJson, "uv_size", 2, null);
                this.uvSetter = UVSetter.constant(textureWidth, textureHeight, uv, uvSize);
            }
        } else {
            this.uvSetter = DEFAULT;
        }
    }

    private static FaceCameraMode parseCameraMode(String type) throws JsonParseException {
        for (FaceCameraMode cameraMode : FaceCameraMode.values())
            if (cameraMode.name.equalsIgnoreCase(type))
                return cameraMode;
        throw new JsonSyntaxException("Unsupported camera mode: " + type + ". Supported camera modes: " + Arrays.stream(FaceCameraMode.values()).map(FaceCameraMode::getName).collect(Collectors.joining(", ")));
    }

    private static SingleQuadRenderProperties getRenderProperties(CustomParticle particle) {
        if (particle.getRenderProperties() instanceof SingleQuadRenderProperties properties)
            return properties;
        SingleQuadRenderProperties properties = new SingleQuadRenderProperties();
        particle.setRenderProperties(properties);
        return properties;
    }

    @Override
    public void render(CustomParticle particle, Camera camera, float partialTicks) {
        MolangEnvironment runtime = particle.getRuntime();
        SingleQuadRenderProperties renderProperties = getRenderProperties(particle);
        renderProperties.setWidth(this.size[0].safeResolve(runtime));
        renderProperties.setHeight(this.size[1].safeResolve(runtime));
        this.uvSetter.setUV(particle, renderProperties);
        switch (this.cameraMode) {
            case ROTATE_XYZ -> renderProperties.setRotation(camera.rotation());
            case ROTATE_Y -> renderProperties.setRotation(Vector3f.YN.rotationDegrees(camera.getYRot()));
            case LOOK_AT_XYZ -> {
                double dx = camera.getPosition().x() - particle.x();
                double dy = camera.getPosition().y() - particle.y();
                double dz = camera.getPosition().z() - particle.z();
                float yRot = (float) Mth.atan2(dz, dx);
                float xRot = (float) Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz));
                Quaternion rotation = renderProperties.getRotation();
                rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
                rotation.mul(Vector3f.YN.rotation((float) (Math.PI / 2F) + yRot));
                rotation.mul(Vector3f.XP.rotation(xRot));
            }
            case LOOK_AT_Y -> {
                double dx = camera.getPosition().x() - particle.x();
                double dz = camera.getPosition().z() - particle.z();
                float yRot = (float) Mth.atan2(dz, dx);
                Quaternion rotation = renderProperties.getRotation();
                rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
                rotation.mul(Vector3f.YN.rotation((float) (Math.PI / 2F) + yRot));
            }
            case DIRECTION_X -> {
                int factor = particle.getSpeed() > this.minSpeedThreshold ? 1 : 0;
                Vec3 direction = particle.getDirection();
                double dx = this.customDirection != null ? this.customDirection[0].safeResolve(runtime) : factor * direction.x();
                double dy = this.customDirection != null ? this.customDirection[1].safeResolve(runtime) : factor * direction.y();
                double dz = this.customDirection != null ? this.customDirection[2].safeResolve(runtime) : factor * direction.z();
                float yRot = (float) Mth.atan2(dz, dx);
                float xRot = (float) Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz));
                Quaternion rotation = renderProperties.getRotation();
                rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
                rotation.mul(Vector3f.YN.rotation(yRot));
                rotation.mul(Vector3f.ZP.rotation(xRot));
            }
            case DIRECTION_Y -> {
                int factor = particle.getSpeed() > this.minSpeedThreshold ? 1 : 0;
                Vec3 direction = particle.getDirection();
                double dx = this.customDirection != null ? this.customDirection[0].safeResolve(runtime) : factor * direction.x();
                double dy = this.customDirection != null ? this.customDirection[1].safeResolve(runtime) : factor * direction.y();
                double dz = this.customDirection != null ? this.customDirection[2].safeResolve(runtime) : factor * direction.z();
                float yRot = (float) Mth.atan2(dz, dx);
                float xRot = (float) Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz));
                Quaternion rotation = renderProperties.getRotation();
                rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
                rotation.mul(Vector3f.YN.rotation(yRot - (float) (Math.PI / 2F)));
                rotation.mul(Vector3f.XN.rotation(xRot - (float) (Math.PI / 2F)));
            }
            case DIRECTION_Z -> {
                int factor = particle.getSpeed() > this.minSpeedThreshold ? 1 : 0;
                Vec3 direction = particle.getDirection();
                double dx = this.customDirection != null ? this.customDirection[0].safeResolve(runtime) : factor * direction.x();
                double dy = this.customDirection != null ? this.customDirection[1].safeResolve(runtime) : factor * direction.y();
                double dz = this.customDirection != null ? this.customDirection[2].safeResolve(runtime) : factor * direction.z();
                float yRot = (float) Mth.atan2(dz, dx);
                float xRot = (float) Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz));
                Quaternion rotation = renderProperties.getRotation();
                rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
                rotation.mul(Vector3f.YN.rotation((float) (Math.PI / 2F) + yRot));
                rotation.mul(Vector3f.XP.rotation(xRot));
            }
        }
    }

    @Override
    public void onCreate(CustomParticle particle) {
        SingleQuadRenderProperties renderProperties = getRenderProperties(particle);
        switch (this.cameraMode) {
            case EMITTER_TRANSFORM_XZ -> renderProperties.setRotation(Vector3f.XP.rotationDegrees(90));
            case EMITTER_TRANSFORM_YZ -> renderProperties.setRotation(Vector3f.YP.rotationDegrees(90));
        }
    }

    @FunctionalInterface
    private interface UVSetter {

        void setUV(CustomParticle particle, SingleQuadRenderProperties properties);

        static UVSetter constant(int textureWidth, int textureHeight, MolangExpression[] uv, MolangExpression[] uvSize) {
            return (particle, properties) -> {
                MolangEnvironment runtime = particle.getRuntime();
                float u0 = uv[0].safeResolve(runtime);
                float v0 = uv[1].safeResolve(runtime);
                float u1 = u0 + uvSize[0].safeResolve(runtime);
                float v1 = v0 + uvSize[1].safeResolve(runtime);
                properties.setUV(u0 / (float) textureWidth, v0 / (float) textureHeight, u1 / (float) textureWidth, v1 / (float) textureHeight);
            };
        }

        static UVSetter flipbook(int textureWidth, int textureHeight, Flipbook flipbook) {
            return (particle, properties) -> properties.setUV(particle.getRuntime(), textureWidth, textureHeight, flipbook, particle.getParticleAge(), particle.getParticleLifetime());
        }
    }

    /**
     * The different types of camera transforms for particles.
     *
     * @author Ocelot
     * @since 1.6.0
     */
    public enum FaceCameraMode {
        ROTATE_XYZ("rotate_xyz"),
        ROTATE_Y("rotate_y"),
        LOOK_AT_XYZ("lookat_xyz"),
        LOOK_AT_Y("lookat_y"),
        DIRECTION_X("direction_x"),
        DIRECTION_Y("direction_y"),
        DIRECTION_Z("direction_z"),
        EMITTER_TRANSFORM_XY("emitter_transform_xy"),
        EMITTER_TRANSFORM_XZ("emitter_transform_xz"),
        EMITTER_TRANSFORM_YZ("emitter_transform_yz");

        private final String name;

        FaceCameraMode(String name) {
            this.name = name;
        }

        /**
         * @return The JSON name of this camera mode
         */
        public String getName() {
            return name;
        }
    }
}
