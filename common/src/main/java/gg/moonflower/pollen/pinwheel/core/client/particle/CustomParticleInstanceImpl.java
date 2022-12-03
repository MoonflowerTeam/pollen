package gg.moonflower.pollen.pinwheel.core.client.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.*;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelRenderer;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryAtlasTexture;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.CustomParticleComponent;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.CustomParticleComponentType;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.CustomParticleRenderComponent;
import gg.moonflower.pollen.pinwheel.api.common.particle.event.ParticleEvent;
import gg.moonflower.pollen.pinwheel.api.common.particle.render.CustomParticleRenderProperties;
import gg.moonflower.pollen.pinwheel.api.common.particle.render.SingleQuadRenderProperties;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTexture;
import gg.moonflower.pollen.pinwheel.api.common.texture.GeometryModelTextureTable;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import io.github.ocelot.molangcompiler.api.bridge.MolangVariable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class CustomParticleInstanceImpl extends CustomParticleImpl {

    public static final ParticleRenderType GEOMETRY_SHEET = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder bufferBuilder, TextureManager textureManager) {
        }

        @Override
        public void end(Tesselator tesselator) {
            GeometryModelRenderer.getCachedBufferSource().endBatch();
            RenderSystem.enableDepthTest();
            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
        }

        public String toString() {
            return "GEOMETRY_SHEET";
        }
    };
    private static final PoseStack MATRIX_STACK = new PoseStack();

    private final CustomParticleEmitter emitter;
    private final Set<CustomParticleRenderComponent> renderComponents;

    @Nullable
    private CustomParticleRenderProperties renderProperties;

    public CustomParticleInstanceImpl(CustomParticleEmitterImpl emitter, ClientLevel clientLevel, double x, double y, double z) {
        super(clientLevel, x, y, z, emitter.getName(), particle -> {
            particle.data.curves().forEach((variable, curve) -> {
                String[] parts = variable.split("\\.", 2);
                String varName = parts.length > 1 ? parts[1] : parts[0];
                particle.variables.put(varName, Pair.of(curve, MolangVariable.create()));
            });
            return MolangRuntime.runtime(emitter.getRuntimeBuilder()).setVariables(particle);
        });
        this.emitter = emitter;
        this.renderComponents = new HashSet<>();
    }

    @Override
    protected void addComponent(CustomParticleComponentType<?> type, CustomParticleComponent instance) {
        super.addComponent(type, instance);
        if (instance instanceof CustomParticleRenderComponent component) {
            this.renderComponents.add(component);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if ((float) this.age / 20.0F >= this.lifetime.getValue()) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
        if (this.age < 0)
            return;
        ProfilerFiller profiler = this.level.getProfiler();
        profiler.push("pollenRenderCustomParticle");
        this.renderAge.setValue((this.age + partialTicks) / 20F);
        this.evaluateCurves();
        profiler.push("updateRenderComponents");
        this.renderComponents.forEach(component -> component.render(this, camera, partialTicks));
        profiler.pop();
        if (this.renderProperties != null) {
            profiler.push("tessellate");
            MATRIX_STACK.pushPose();
            Vec3 pos = camera.getPosition();
            float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - pos.x());
            float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - pos.y());
            float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - pos.z());
            MATRIX_STACK.translate(x, y, z);
            if (this.renderProperties instanceof SingleQuadRenderProperties properties && properties.canRender()) {
                GeometryAtlasTexture atlas = GeometryTextureManager.getAtlas();
                GeometryModelTextureTable table = GeometryTextureManager.getTextures(this.data.description().material());
                GeometryModelTexture[] textures = table.getLayerTextures(this.data.description().texture());

                float zRot = Mth.lerp(partialTicks, this.oRoll, this.roll);
                MATRIX_STACK.pushPose();
                MATRIX_STACK.mulPose(properties.getRotation());
                MATRIX_STACK.mulPose(Vector3f.ZP.rotationDegrees(zRot));
                MATRIX_STACK.scale(properties.getWidth(), properties.getHeight(), 1.0F);
                for (GeometryModelTexture texture : textures) {
                    VertexConsumer consumer = GeometryModelRenderer.getCachedBufferSource().getBuffer(texture.getLayer().getRenderType(texture, atlas, null));
                    this.renderQuad(atlas.getSprite(texture.getLocation()).wrap(consumer), properties, texture.getRed(), texture.getGreen(), texture.getBlue(), partialTicks);
                }
                MATRIX_STACK.popPose();
            }
            MATRIX_STACK.popPose();
            profiler.pop();
        }
        profiler.pop();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return GEOMETRY_SHEET;
    }

    private void renderQuad(VertexConsumer consumer, SingleQuadRenderProperties properties, float red, float green, float blue, float partialTicks) {
        float uMin = properties.getUMin();
        float uMax = properties.getUMax();
        float vMin = properties.getVMin();
        float vMax = properties.getVMax();
        float r = properties.getRed() * red;
        float g = properties.getGreen() * green;
        float b = properties.getBlue() * blue;
        float a = properties.getAlpha();
        int light = properties.getPackedLight();

        Matrix4f matrix4f = MATRIX_STACK.last().pose();
        Matrix3f matrix3f = MATRIX_STACK.last().normal();

        Vector4f vector4f = new Vector4f(-1.0F, -1.0F, 0.0F, 1.0F);
        vector4f.transform(matrix4f);
        consumer.vertex(matrix4f, -1.0F, -1.0F, 0.0F);
        consumer.color(r, g, b, a);
        consumer.uv(uMax, vMax);
        consumer.overlayCoords(OverlayTexture.NO_OVERLAY);
        consumer.uv2(light);
        consumer.normal(matrix3f, 0, 1, 0);
        consumer.endVertex();

        consumer.vertex(matrix4f, -1.0F, 1.0F, 0.0F);
        consumer.color(r, g, b, a);
        consumer.uv(uMax, vMin);
        consumer.overlayCoords(OverlayTexture.NO_OVERLAY);
        consumer.uv2(light);
        consumer.normal(matrix3f, 0, 1, 0);
        consumer.endVertex();

        consumer.vertex(matrix4f, 1.0F, 1.0F, 0.0F);
        consumer.color(r, g, b, a);
        consumer.uv(uMin, vMin);
        consumer.overlayCoords(OverlayTexture.NO_OVERLAY);
        consumer.uv2(light);
        consumer.normal(matrix3f, 0, 1, 0);
        consumer.endVertex();

        consumer.vertex(matrix4f, 1.0F, -1.0F, 0.0F);
        consumer.color(r, g, b, a);
        consumer.uv(uMin, vMax);
        consumer.overlayCoords(OverlayTexture.NO_OVERLAY);
        consumer.uv2(light);
        consumer.normal(matrix3f, 0, 1, 0);
        consumer.endVertex();
    }

    @Override
    protected Component getPrefix() {
        return new TextComponent("").append(new TextComponent("[Particle]").withStyle(ChatFormatting.YELLOW)).append(super.getPrefix());
    }

    @Override
    public void addMolangVariables(Context context) {
        super.addMolangVariables(context);
        context.addVariable("particle_age", this.renderAge);
        context.addVariable("particle_lifetime", this.lifetime);
        context.addVariable("particle_random_1", this.random1);
        context.addVariable("particle_random_2", this.random2);
        context.addVariable("particle_random_3", this.random3);
        context.addVariable("particle_random_4", this.random4);
    }

    @Override
    public void particleEffect(String effect, ParticleEvent.ParticleSpawnType type) {
        switch (type) {
            case PARTICLE -> {
                ParticleOptions options = this.getOptions(effect);
                if (options != null)
                    this.level.addParticle(options, this.x, this.y, this.z, 0, 0, 0);
            }
            case PARTICLE_WITH_VELOCITY -> {
                ParticleOptions options = this.getOptions(effect);
                if (options != null)
                    this.level.addParticle(options, this.x, this.y, this.z, this.xd, this.yd, this.zd);
            }
        }
    }

    @Nullable
    @Override
    public CustomParticleRenderProperties getRenderProperties() {
        return renderProperties;
    }

    @Override
    public void setRenderProperties(@Nullable CustomParticleRenderProperties properties) {
        this.renderProperties = properties;
    }

    @Override
    public boolean isParticle() {
        return true;
    }

    @Override
    public CustomParticleEmitter getEmitter() {
        return emitter;
    }
}
