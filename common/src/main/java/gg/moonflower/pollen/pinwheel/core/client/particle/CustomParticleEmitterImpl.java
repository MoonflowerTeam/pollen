package gg.moonflower.pollen.pinwheel.core.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import gg.moonflower.pollen.api.particle.CustomParticleOption;
import gg.moonflower.pollen.api.particle.PollenParticleComponents;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleEmitter;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.CustomParticleComponent;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.CustomParticleComponentType;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.ParticleLifetimeEventComponent;
import gg.moonflower.pollen.pinwheel.api.common.particle.event.ParticleEvent;
import gg.moonflower.pollen.pinwheel.api.common.particle.listener.CustomEmitterListener;
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
public class CustomParticleEmitterImpl extends CustomParticleImpl implements CustomParticleEmitter {

    private final Set<CustomEmitterListener> listeners;
    private final Set<CustomParticle> particles;
    private final boolean allowLoop;

    public CustomParticleEmitterImpl(ClientLevel clientLevel, double x, double y, double z, ResourceLocation name, boolean allowLoop) {
        super(clientLevel, x, y, z, name, particle -> {
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
        this.listeners = new HashSet<>();
        this.particles = new HashSet<>();
        this.allowLoop = allowLoop;
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
        if (this.allowLoop) {
            this.age = 0;
            this.lifetime.setValue(0);
            this.particles.forEach(CustomParticle::expire);
            this.particles.clear();
            this.listeners.forEach(listener -> listener.onLoop(this));
        } else {
            this.remove();
        }
    }

    @Override
    public CustomParticle newParticle() {
        CustomParticleInstanceImpl particle = new CustomParticleInstanceImpl(this, this.level, this.x, this.y, this.z);
        this.childComponents.forEach(particle::addComponent); // Pass components to children
        return particle;
    }

    @Override
    public void summonParticle(CustomParticle particle, double x, double y, double z, double dx, double dy, double dz) {
        particle.setPos(particle.x() + x, particle.y() + y, particle.z() + z);
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

    @Override
    public void particleEffect(String effect, ParticleEvent.ParticleSpawnType type) {
        switch (type) {
            case EMITTER, EMITTER_BOUND -> {
                ParticleOptions options = this.getOptions(effect);
                if (options != null)
                    this.level.addParticle(options, this.x, this.y, this.z, 0, 0, 0);
            }
        }
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

    public static class Provider implements ParticleProvider<CustomParticleOption> {

        @Override
        public Particle createParticle(CustomParticleOption type, ClientLevel clientLevel, double x, double y, double z, double motionX, double motionY, double motionZ) {
            return new CustomParticleEmitterImpl(clientLevel, x, y, z, type.getName(), false);
        }
    }
}
