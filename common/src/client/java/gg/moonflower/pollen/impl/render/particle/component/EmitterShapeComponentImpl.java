package gg.moonflower.pollen.impl.render.particle.component;

import gg.moonflower.pinwheel.api.particle.ParticleInstance;
import gg.moonflower.pinwheel.api.particle.ParticleSourceObject;
import gg.moonflower.pinwheel.api.particle.component.ParticleEmitterShape;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleEmitter;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleEmitterListener;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.Random;

@ApiStatus.Internal
public class EmitterShapeComponentImpl extends BedrockParticleEmitterComponentImpl implements BedrockParticleEmitterListener, ParticleEmitterShape.Spawner {

    private final ParticleEmitterShape data;
    private final Source source;

    public EmitterShapeComponentImpl(BedrockParticle particle, ParticleEmitterShape data) {
        super(particle);
        this.data = data;
        this.source = new Source();
    }

    @Override
    public void onEmitParticles(BedrockParticleEmitter emitter, int count) {
        this.data.emitParticles(this, count);
    }

    @Override
    public BedrockParticle createParticle() {
        return this.particle.newParticle();
    }

    @Override
    public void spawnParticle(ParticleInstance instance) {
        if (!(instance instanceof BedrockParticle particle)) {
            throw new AssertionError();
        }

        Vector3dc pos = particle.position();
        this.particle.summonParticle(particle, pos.x(), pos.y(), pos.z());
    }

    @Override
    public @Nullable ParticleSourceObject getEntity() {
        Entity entity = this.particle.getEntity();
        if (entity == null) {
            return null;
        }

        this.source.bounds = entity.getBoundingBox();
        return this.source;
    }

    @Override
    public MolangEnvironment getEnvironment() {
        return this.particle.getEnvironment();
    }

    @Override
    public Random getRandom() {
        return this.particle.getRandom();
    }

    @Override
    public void setPosition(ParticleInstance instance, double x, double y, double z) {
        if (!(instance instanceof BedrockParticle particle)) {
            throw new AssertionError();
        }

        particle.setPosition(x, y, z);
    }

    @Override
    public void setVelocity(ParticleInstance instance, double dx, double dy, double dz) {
        if (!(instance instanceof BedrockParticle particle)) {
            throw new AssertionError();
        }

        BedrockParticlePhysics physics = particle.getPhysics();
        if (physics == null) {
            return;
        }

        BedrockParticlePhysics emitterPhysics = this.particle.getPhysics();
        if (emitterPhysics != null) {
            Vector3dc velocity = emitterPhysics.getVelocity();
            dx += velocity.x();
            dy += velocity.y();
            dz += velocity.z();
        }

        physics.setVelocity(new Vector3d(dx, dy, dz));
    }

    private static class Source implements ParticleSourceObject {

        private AABB bounds;

        @Override
        public float getMinX() {
            return (float) this.bounds.minX;
        }

        @Override
        public float getMinY() {
            return (float) this.bounds.minY;
        }

        @Override
        public float getMinZ() {
            return (float) this.bounds.minZ;
        }

        @Override
        public float getMaxX() {
            return (float) this.bounds.maxX;
        }

        @Override
        public float getMaxY() {
            return (float) this.bounds.maxY;
        }

        @Override
        public float getMaxZ() {
            return (float) this.bounds.maxZ;
        }
    }
}
