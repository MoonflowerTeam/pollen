package gg.moonflower.pollen.pinwheel.core.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.event.events.registry.client.ParticleFactoryRegistryEvent;
import gg.moonflower.pollen.api.particle.CustomParticleOption;
import gg.moonflower.pollen.api.particle.PollenParticleComponents;
import gg.moonflower.pollen.api.particle.PollenParticles;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.CustomParticleComponent;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.CustomParticleComponentType;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.CustomEmitterListener;
import gg.moonflower.pollen.pinwheel.api.common.particle.render.CustomParticleRenderProperties;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import io.github.ocelot.molangcompiler.api.bridge.MolangVariable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class CustomParticleEmitterImpl extends CustomParticleImpl implements CustomParticleEmitter {

    @Nullable
    private final Entity entity;
    private final Set<CustomEmitterListener> listeners;
    private final Set<CustomParticle> particles;

    public CustomParticleEmitterImpl(@NotNull Entity entity, ResourceLocation name) {
        this(entity, (ClientLevel) entity.level, entity.getX(), entity.getY(), entity.getZ(), name);
    }

    public CustomParticleEmitterImpl(@Nullable Entity entity, ClientLevel level, double x, double y, double z, ResourceLocation name) {
        super(level, x, y, z, name, particle -> {
            particle.data.curves().forEach((variable, curve) -> {
                String[] parts = variable.split("\\.", 2);
                String varName = parts.length > 1 ? parts[1] : parts[0];
                particle.variables.put(varName, Pair.of(curve, MolangVariable.create()));
            });
            particle.data.components().forEach((component, json) -> {
                ResourceLocation id = ResourceLocation.tryParse(component);
                CustomParticleComponentType<?> type = PollenParticleComponents.COMPONENTS.get(id);
                if (type == null) {
                    LOGGER.warn(particle.getPrefix().getString() + "Unknown component: {}", id != null ? id : component);
                    return;
                }
                try {
                    CustomParticleComponent instance = type.create(json);
                    particle.addComponent(type, instance);
                    if (instance instanceof CustomEmitterListener listener) {
                        ((CustomParticleEmitterImpl) particle).addEmitterListener(listener);
                    }
                } catch (Exception e) {
                    LOGGER.error(particle.getPrefix().getString() + "Failed to create component: {}", component, e);
                }
            });
            return MolangRuntime.runtime().setVariable("entity_scale", MolangVariable.create(1.0F)).setVariables(particle);
        });
        this.entity = entity;
        this.listeners = new HashSet<>();
        this.particles = new HashSet<>();
    }

    @Override
    protected void addComponent(CustomParticleComponentType<?> type, CustomParticleComponent instance) {
        super.addComponent(type, instance);
        this.childComponents.put(type, instance);
    }

    @Override
    public void addEmitterListener(CustomEmitterListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeEmitterListener(CustomEmitterListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void tick() {
        super.tick();
        this.renderAge.setValue((float) this.age / 20F);
        this.particles.removeIf(CustomParticle::isExpired);
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
    public CustomParticle newParticle() {
        CustomParticleInstanceImpl particle = new CustomParticleInstanceImpl(this, this.level, this.x, this.y, this.z);
        this.childComponents.forEach(particle::addComponent); // Pass components to children
        return particle;
    }

    @Override
    public void summonParticle(CustomParticle particle, double x, double y, double z, double dx, double dy, double dz) {
        particle.setPosition(particle.x() + x, particle.y() + y, particle.z() + z);
        particle.setDirection(new Vec3(dx, dy, dz));
        if (!(particle instanceof Particle p))
            throw new UnsupportedOperationException(particle.getName() + " must extent net.minecraft.client.particle.Particle");
        this.particles.add(particle);
        Minecraft.getInstance().particleEngine.add(p);
    }

    @Override
    public void summonParticle(ParticleOptions particle, double x, double y, double z, double dx, double dy, double dz) {
        this.level.addParticle(particle, this.x + x, this.y + y, this.z + z, dx, dy, dz);
    }

    @Override
    public void setLifetime(float time) {
        if (this.lifetime.getValue() < time || time == 0)
            this.lifetime.setValue(time);
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
        return new TextComponent("").append(new TextComponent("[Emitter]").withStyle(ChatFormatting.YELLOW)).append(super.getPrefix());
    }

    @Override
    public void addMolangVariables(Context context) {
        super.addMolangVariables(context);
        context.addVariable("emitter_age", this.renderAge);
        context.addVariable("emitter_lifetime", this.lifetime);
        context.addVariable("emitter_random_1", this.random1);
        context.addVariable("emitter_random_2", this.random2);
        context.addVariable("emitter_random_3", this.random3);
        context.addVariable("emitter_random_4", this.random4);
    }

    @Nullable
    @Override
    public CustomParticleRenderProperties getRenderProperties() {
        return null;
    }

    @Override
    public void setRenderProperties(@Nullable CustomParticleRenderProperties properties) {
    }

    @Override
    public boolean isParticle() {
        return false;
    }

    @Override
    public CustomParticleEmitter getEmitter() {
        return this;
    }

    public static void registerFactory(ParticleFactoryRegistryEvent.Registry registry) {
        registry.register(PollenParticles.CUSTOM.get(), new CustomParticleEmitterImpl.Provider());
    }

    public static class Provider implements ParticleProvider<CustomParticleOption> {

        @Override
        public Particle createParticle(CustomParticleOption type, ClientLevel level, double x, double y, double z, double motionX, double motionY, double motionZ) {
            return new CustomParticleEmitterImpl(null, level, x, y, z, type.getName());
        }
    }
}
