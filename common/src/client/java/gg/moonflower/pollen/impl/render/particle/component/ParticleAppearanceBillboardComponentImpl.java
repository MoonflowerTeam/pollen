package gg.moonflower.pollen.impl.render.particle.component;

import com.mojang.math.Vector3f;
import gg.moonflower.pinwheel.api.particle.component.ParticleAppearanceBillboardComponent;
import gg.moonflower.pinwheel.api.particle.render.SingleQuadRenderProperties;
import gg.moonflower.pollen.api.joml.v1.JomlBridge;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.MinecraftSingleQuadRenderProperties;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleRenderComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import gg.moonflower.pollen.impl.render.particle.MinecraftSingleQuadRenderPropertiesImpl;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Quaternionf;
import org.joml.Vector3dc;

@ApiStatus.Internal
public class ParticleAppearanceBillboardComponentImpl extends BedrockParticleComponentImpl implements BedrockParticleListener, BedrockParticleRenderComponent {

    private final ParticleAppearanceBillboardComponent data;

    public ParticleAppearanceBillboardComponentImpl(BedrockParticle particle, ParticleAppearanceBillboardComponent data) {
        super(particle);
        this.data = data;
    }

    private static SingleQuadRenderProperties getRenderProperties(BedrockParticle particle) {
        if (particle.getRenderProperties() instanceof MinecraftSingleQuadRenderProperties properties) {
            return properties;
        }
        MinecraftSingleQuadRenderProperties properties = new MinecraftSingleQuadRenderPropertiesImpl();
        particle.setRenderProperties(properties);
        return properties;
    }

    @Override
    public void render(Camera camera, float partialTicks) {
        MolangEnvironment environment = this.particle.getEnvironment();
        SingleQuadRenderProperties renderProperties = getRenderProperties(particle);
        renderProperties.setWidth(this.data.size()[0].safeResolve(environment));
        renderProperties.setHeight(this.data.size()[1].safeResolve(environment));
        this.data.textureSetter().setUV(this.particle, environment, renderProperties);

        switch (this.data.cameraMode()) {
            case ROTATE_XYZ -> JomlBridge.set(renderProperties.getRotation(), camera.rotation());
            case ROTATE_Y ->
                    JomlBridge.set(renderProperties.getRotation(), Vector3f.YN.rotationDegrees(camera.getYRot()));
            case LOOK_AT_XYZ -> {
                Vector3dc pos = this.particle.position(partialTicks);
                double dx = camera.getPosition().x() - pos.x();
                double dy = camera.getPosition().y() - pos.y();
                double dz = camera.getPosition().z() - pos.z();
                float yRot = (float) Mth.atan2(dz, dx);
                float xRot = (float) Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz));
                Quaternionf rotation = renderProperties.getRotation();
                rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
                rotation.rotateZYX(0, -((float) (Math.PI / 2F) + yRot), xRot);
            }
            case LOOK_AT_Y -> {
                Vector3dc pos = this.particle.position(partialTicks);
                double dx = camera.getPosition().x() - pos.x();
                double dz = camera.getPosition().z() - pos.z();
                float yRot = (float) Mth.atan2(dz, dx);
                Quaternionf rotation = renderProperties.getRotation();
                rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
                rotation.rotateY(-((float) (Math.PI / 2F) + yRot));
            }
            case DIRECTION_X -> {
                BedrockParticlePhysics physics = this.particle.getPhysics();
                if (physics == null) {
                    return;
                }

                double dx;
                double dy;
                double dz;

                MolangExpression[] customDirection = this.data.customDirection();
                if (customDirection != null) {
                    dx = customDirection[0].safeResolve(environment);
                    dy = customDirection[1].safeResolve(environment);
                    dz = customDirection[2].safeResolve(environment);
                } else {
                    Vector3dc direction = physics.getDirection();
                    int factor = physics.getSpeed() > this.data.minSpeedThreshold() ? 1 : 0;
                    dx = factor * direction.x();
                    dy = factor * direction.y();
                    dz = factor * direction.z();
                }

                float yRot = (float) Mth.atan2(dz, dx);
                float xRot = (float) Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz));
                Quaternionf rotation = renderProperties.getRotation();
                rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
                rotation.rotateZYX(0, -yRot, xRot);
            }
            case DIRECTION_Y -> {
                BedrockParticlePhysics physics = this.particle.getPhysics();
                if (physics == null) {
                    return;
                }

                double dx;
                double dy;
                double dz;

                MolangExpression[] customDirection = this.data.customDirection();
                if (customDirection != null) {
                    dx = customDirection[0].safeResolve(environment);
                    dy = customDirection[1].safeResolve(environment);
                    dz = customDirection[2].safeResolve(environment);
                } else {
                    Vector3dc direction = physics.getDirection();
                    int factor = physics.getSpeed() > this.data.minSpeedThreshold() ? 1 : 0;
                    dx = factor * direction.x();
                    dy = factor * direction.y();
                    dz = factor * direction.z();
                }

                float yRot = (float) Mth.atan2(dz, dx);
                float xRot = (float) Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz));
                Quaternionf rotation = renderProperties.getRotation();
                rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
                rotation.rotateZYX(0, -(yRot - (float) (Math.PI / 2F)), -(xRot - (float) (Math.PI / 2F)));
            }
            case DIRECTION_Z -> {
                BedrockParticlePhysics physics = this.particle.getPhysics();
                if (physics == null) {
                    return;
                }

                double dx;
                double dy;
                double dz;

                MolangExpression[] customDirection = this.data.customDirection();
                if (customDirection != null) {
                    dx = customDirection[0].safeResolve(environment);
                    dy = customDirection[1].safeResolve(environment);
                    dz = customDirection[2].safeResolve(environment);
                } else {
                    Vector3dc direction = physics.getDirection();
                    int factor = physics.getSpeed() > this.data.minSpeedThreshold() ? 1 : 0;
                    dx = factor * direction.x();
                    dy = factor * direction.y();
                    dz = factor * direction.z();
                }

                float yRot = (float) Mth.atan2(dz, dx);
                float xRot = (float) Mth.atan2(dy, Math.sqrt(dx * dx + dz * dz));
                Quaternionf rotation = renderProperties.getRotation();
                rotation.set(0.0F, 0.0F, 0.0F, 1.0F);
                rotation.rotateZYX(0, -((float) (Math.PI / 2F) + yRot), xRot);
            }
        }
    }

    @Override
    public void onCreate(BedrockParticle particle) {
        SingleQuadRenderProperties renderProperties = getRenderProperties(particle);
        switch (this.data.cameraMode()) {
            case EMITTER_TRANSFORM_XZ ->
                    JomlBridge.set(renderProperties.getRotation(), Vector3f.XP.rotationDegrees(90));
            case EMITTER_TRANSFORM_YZ ->
                    JomlBridge.set(renderProperties.getRotation(), Vector3f.YP.rotationDegrees(90));
        }
    }
}
