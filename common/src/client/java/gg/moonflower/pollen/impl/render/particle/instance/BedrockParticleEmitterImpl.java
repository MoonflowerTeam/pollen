package gg.moonflower.pollen.impl.render.particle.instance;

import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pinwheel.api.particle.component.ParticleComponent;
import gg.moonflower.pinwheel.api.particle.render.ParticleRenderProperties;
import gg.moonflower.pollen.api.registry.particle.v1.BedrockParticleComponentFactory;
import gg.moonflower.pollen.api.registry.particle.v1.BedrockParticleComponentType;
import gg.moonflower.pollen.api.registry.particle.v1.BedrockParticleComponents;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleComponent;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleEmitterListener;
import gg.moonflower.pollen.impl.particle.BedrockParticleOption;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import io.github.ocelot.molangcompiler.api.bridge.MolangVariable;
import io.github.ocelot.molangcompiler.api.bridge.MolangVariableProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class BedrockParticleEmitterImpl extends BedrockParticleImpl implements BedrockParticleEmitter {

    @Nullable
    private final Entity entity;
    private final Set<BedrockParticleEmitterListener> listeners;
    private final Set<BedrockParticle> particles;

    public BedrockParticleEmitterImpl(@NotNull Entity entity, ResourceLocation name) {
        this(entity, (ClientLevel) entity.level, entity.getX(), entity.getY(), entity.getZ(), name);
    }

    public BedrockParticleEmitterImpl(@Nullable Entity entity, ClientLevel level, double x, double y, double z, ResourceLocation name) {
        super(level, x, y, z, name, particle -> MolangRuntime.runtime().setVariable("entity_scale", MolangVariable.create(1.0F)).setVariables(particle));
        this.entity = entity;
        this.listeners = new HashSet<>();
        this.particles = new HashSet<>();

        this.data.components().forEach((component, data) -> {
            ResourceLocation id = ResourceLocation.tryParse(component);
            if (id == null) {
                LOGGER.warn(this.getPrefix().getString() + "Invalid component id: {}", component);
                return;
            }

            BedrockParticleComponentType<?> type = BedrockParticleComponents.COMPONENTS.getRegistrar().get(id);
            if (type == null) {
                LOGGER.warn(this.getPrefix().getString() + "Unknown component: {}", id);
                return;
            }

            try {
                this.addComponent(type.componentFactory(), data);
            } catch (Exception e) {
                LOGGER.error(this.getPrefix().getString() + "Failed to create component: {}", component, e);
            }
        });
    }

    @Override
    protected @Nullable BedrockParticleComponent addComponent(BedrockParticleComponentFactory<?> type, ParticleComponent data) {
        BedrockParticleComponent component = super.addComponent(type, data);
        if (component instanceof BedrockParticleEmitterListener listener) {
            this.addEmitterListener(listener);
        }
        return component;
    }

    @Override
    public void addEmitterListener(BedrockParticleEmitterListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeEmitterListener(BedrockParticleEmitterListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void tick() {
        super.tick();
        this.renderAge.setValue((float) this.age / 20F);
        this.particles.removeIf(BedrockParticle::isExpired);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float f) {
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.NO_RENDER;
    }

    @Override
    public void emitParticles(int count) {
        this.listeners.forEach(listener -> listener.onEmitParticles(this, count));
    }

    @Override
    public void restart() {
        this.age = 0;
        this.lifetime.setValue(0);
        this.random1.setValue(this.random.nextFloat());
        this.random2.setValue(this.random.nextFloat());
        this.random3.setValue(this.random.nextFloat());
        this.random4.setValue(this.random.nextFloat());
        this.listeners.forEach(listener -> listener.onLoop(this));
    }

    @Override
    public BedrockParticle newParticle() {
        return new BedrockParticleInstanceImpl(this, this.level, this.x, this.y, this.z);
    }

    @Override
    public void summonParticle(BedrockParticle particle, double x, double y, double z, double dx, double dy, double dz) {
        if (!(particle instanceof Particle p)) {
            throw new UnsupportedOperationException(particle.getName() + " must extent net.minecraft.client.particle.Particle");
        }

        Vector3dc pos = particle.position();
        particle.setPosition(pos.x() + x, pos.y() + y, pos.z() + z);

        BedrockParticlePhysics physics = particle.getPhysics();
        if (physics != null) {
            physics.setDirection(dx, dy, dz);
        }

        this.particles.add(particle);
        Minecraft.getInstance().particleEngine.add(p);
    }

    @Override
    public void summonParticle(ParticleOptions particle, double x, double y, double z, double dx, double dy, double dz) {
        this.level.addParticle(particle, this.x + x, this.y + y, this.z + z, dx, dy, dz);
    }

    @Override
    public void setLifetime(float time) {
        if (this.lifetime.getValue() < time || time == 0) {
            this.lifetime.setValue(time);
        }
    }

    @Override
    public int getSpawnedParticles() {
        return this.particles.size();
    }

    @Nullable
    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    protected Component getPrefix() {
        return Component.empty().append(Component.literal("[Emitter]").withStyle(ChatFormatting.YELLOW)).append(super.getPrefix());
    }

    @Override
    public void addMolangVariables(MolangVariableProvider.Context context) {
        context.addVariable("emitter_age", this.renderAge);
        context.addVariable("emitter_lifetime", this.lifetime);
        context.addVariable("emitter_random_1", this.random1);
        context.addVariable("emitter_random_2", this.random2);
        context.addVariable("emitter_random_3", this.random3);
        context.addVariable("emitter_random_4", this.random4);
    }

    @Override
    public @Nullable ParticleRenderProperties getRenderProperties() {
        return null;
    }

    @Override
    public void setRenderProperties(@Nullable ParticleRenderProperties properties) {
    }

    @Override
    public BedrockParticleEmitter getEmitter() {
        return this;
    }

    public static class Provider implements ParticleProvider<BedrockParticleOption> {

        @Override
        public Particle createParticle(BedrockParticleOption type, ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ) {
            return new BedrockParticleEmitterImpl(null, level, x, y, z, type.getName());
        }
    }
}
