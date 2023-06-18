package gg.moonflower.pollen.impl.render.particle.instance;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.bridge.MolangVariable;
import gg.moonflower.molangcompiler.api.bridge.MolangVariableProvider;
import gg.moonflower.pinwheel.api.particle.ParticleData;
import gg.moonflower.pinwheel.api.particle.ParticleEvent;
import gg.moonflower.pinwheel.api.particle.component.ParticleComponent;
import gg.moonflower.pollen.api.registry.particle.v1.BedrockParticleComponentFactory;
import gg.moonflower.pollen.api.registry.particle.v1.BedrockParticleComponentType;
import gg.moonflower.pollen.api.registry.particle.v1.BedrockParticleComponents;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticle;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleCurves;
import gg.moonflower.pollen.api.render.particle.v1.BedrockParticleManager;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleComponent;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysics;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticlePhysicsComponent;
import gg.moonflower.pollen.api.render.particle.v1.component.BedrockParticleTickComponent;
import gg.moonflower.pollen.api.render.particle.v1.listener.BedrockParticleListener;
import gg.moonflower.pollen.impl.render.particle.BedrockParticlePhysicsImpl;
import gg.moonflower.pollen.impl.render.particle.ProfilingMolangEnvironment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public abstract class BedrockParticleImpl extends Particle implements BedrockParticle, MolangVariableProvider {

    private static final Set<VoxelShape> COLLISION_SHAPES = new HashSet<>();
    protected static final Logger LOGGER = LoggerFactory.getLogger(BedrockParticle.class);
    protected static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);

    protected final ResourceLocation name;
    protected final ParticleData data;
    protected final BedrockParticleCurves curves;
    protected final MolangEnvironment environment;
    protected final Random random;

    protected final MolangVariable renderAge;
    protected final MolangVariable lifetime;
    protected final MolangVariable random1;
    protected final MolangVariable random2;
    protected final MolangVariable random3;
    protected final MolangVariable random4;

    private final Set<BedrockParticleListener> listeners;
    private final Set<BedrockParticleTickComponent> tickComponents;
    private final Set<BedrockParticlePhysicsComponent> physicsComponents;

    private final Vector3d pos;
    private final Vector3d renderPos;
    private final BlockPos.MutableBlockPos blockPos;
    private final Vector3d delta;
    private BedrockParticlePhysicsImpl physics;
    private float radius;

    protected int age;
    protected boolean disableMovement;

    protected BedrockParticleImpl(ClientLevel level, double x, double y, double z, ResourceLocation name) {
        super(level, x, y, z);
        this.name = name;
        this.data = BedrockParticleManager.getParticle(this.name);
        this.curves = new BedrockParticleCurves(this.data);
        this.environment = new ProfilingMolangEnvironment(MolangRuntime.runtime().setVariables(this.curves).create(), level.getProfilerSupplier());
        this.random = new Random();
        this.renderAge = MolangVariable.create();
        this.lifetime = MolangVariable.create();
        this.random1 = MolangVariable.create(this.random.nextFloat());
        this.random2 = MolangVariable.create(this.random.nextFloat());
        this.random3 = MolangVariable.create(this.random.nextFloat());
        this.random4 = MolangVariable.create(this.random.nextFloat());

        this.listeners = new HashSet<>();
        this.tickComponents = new HashSet<>();
        this.physicsComponents = new HashSet<>();

        this.pos = new Vector3d();
        this.renderPos = new Vector3d();
        this.blockPos = new BlockPos.MutableBlockPos();
        this.delta = new Vector3d();
        this.oRoll = 0;
        this.roll = 0;

        this.physics = null;
        this.radius = 0.1F;
        this.age = -1;
    }

    protected void addComponents() {
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected @Nullable BedrockParticleComponent addComponent(BedrockParticleComponentFactory<?> type, ParticleComponent data) {
        if (!type.isValid(this)) {
            return null;
        }

        BedrockParticleComponent component = ((BedrockParticleComponentFactory) type).create(this, data);
        if (component instanceof BedrockParticleListener listener) {
            this.addListener(listener);
        }
        if (component instanceof BedrockParticleTickComponent tickComponent) {
            this.tickComponents.add(tickComponent);
        }
        if (component instanceof BedrockParticlePhysicsComponent tickComponent) {
            this.physicsComponents.add(tickComponent);
            if (this.physics == null) {
                this.physics = new BedrockParticlePhysicsImpl();
            }
        }

        return component;
    }

    protected Component getPrefix() {
        return Component.empty().append(Component.literal("[" + this.name + "]").withStyle(ChatFormatting.AQUA)).append(" ");
    }

    @Override
    public void tick() {
        ProfilerFiller profiler = this.level.getProfiler();
        profiler.push("pollen");

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.oRoll = this.roll;

        // Init
        if (this.age == -1) {
            this.age = 0;
            profiler.push("init");
            this.listeners.forEach(listener -> listener.onCreate(this));
            profiler.pop();
        }

        this.curves.evaluate(this.getEnvironment(), profiler);

        // Tick components
        profiler.push("components");
        this.tickComponents.forEach(BedrockParticleTickComponent::tick);
        profiler.pop();

        if (this.physics != null) {
            this.hasPhysics = this.physics.hasCollision();
            if (this.hasPhysics) {
                profiler.push("physics");

                float radius = this.physics.getCollisionRadius();
                if (this.radius != radius) {
                    this.radius = radius;
                    this.setSize(radius * 2, radius * 2);
                }

                if (!this.disableMovement) { // Stop moving particles if they collide and effectively stop because of it
                    profiler.push("components");
                    this.physicsComponents.forEach(BedrockParticlePhysicsComponent::physicsTick);
                    profiler.popPush("move");
                    this.physics.tick();

                    float speed = this.physics.getSpeed();
                    if (speed * speed > 1.0E-7) {
                        Vector3dc direction = this.physics.getDirection();
                        this.move(direction.x() * speed, direction.y() * speed, direction.z() * speed);
                    }

                    profiler.pop();
                }
                profiler.pop();
            }
        }

        // Tick age
        this.age++;

        profiler.pop();
    }

    @Override
    public void move(double dx, double dy, double dz) {
        double g = dx;
        double h = dy;
        double i = dz;
        if (this.physics != null && (dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
            collideBoundingBox(this.delta.set(dx, dy, dz), this.getBoundingBox(), this.level);
            dx = this.delta.x();
            dy = this.delta.y();
            dz = this.delta.z();
        }

        if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
            this.setLocationFromBoundingbox();
            for (BedrockParticleListener listener : this.listeners) {
                listener.onMove(this, dx, dy, dz);
            }
        }

        if (this.physics != null) {
            boolean xCollision = Math.abs(g) >= 1.0E-5F && Math.abs(dx) < 1.0E-5F;
            boolean yCollision = Math.abs(h) >= 1.0E-5F && Math.abs(dy) < 1.0E-5F;
            boolean zCollision = Math.abs(i) >= 1.0E-5F && Math.abs(dz) < 1.0E-5F;
            this.onGround = h != dy && h < 0.0;

            if (xCollision || yCollision || zCollision) {
                this.listeners.forEach(listener -> listener.onCollide(this, xCollision, yCollision, zCollision));
                if (this.onGround && this.physics.getSpeed() * this.physics.getSpeed() <= 1.0E-7) {
                    this.disableMovement = true;
                }
            }
        }
    }

    private static synchronized void collideBoundingBox(Vector3d delta, AABB box, Level level) {
        COLLISION_SHAPES.clear();
        level.getBlockCollisions(null, box.expandTowards(delta.x(), delta.y(), delta.z())).forEach(COLLISION_SHAPES::add);
        // TODO reuse block collision shapes between particles
        if (COLLISION_SHAPES.isEmpty()) {
            return;
        }

        double x = delta.x();
        double y = delta.y();
        double z = delta.z();
        if (y != 0.0) {
            y = Shapes.collide(Direction.Axis.Y, box, COLLISION_SHAPES, y);
            if (y != 0.0) {
                box = box.move(0.0, y, 0.0);
            }
        }

        boolean zCollide = Math.abs(x) < Math.abs(z);
        if (zCollide && z != 0.0) {
            z = Shapes.collide(Direction.Axis.Z, box, COLLISION_SHAPES, z);
            if (z != 0.0) {
                box = box.move(0.0, 0.0, z);
            }
        }

        if (x != 0.0) {
            x = Shapes.collide(Direction.Axis.X, box, COLLISION_SHAPES, x);
            if (!zCollide && x != 0.0) {
                box = box.move(x, 0.0, 0.0);
            }
        }

        if (!zCollide && z != 0.0) {
            z = Shapes.collide(Direction.Axis.Z, box, COLLISION_SHAPES, z);
        }

        delta.set(x, y, z);
    }

    @Override
    public void remove() {
        super.remove();
        this.listeners.forEach(listener -> listener.onExpire(this));
    }

    @Override
    public void addListener(BedrockParticleListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(BedrockParticleListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public void runEvent(String name) {
        if (this.data.events().containsKey(name)) {
            this.data.events().get(name).execute(this);
        }
    }

    @Override
    public void particleEffect(String effect, ParticleEvent.ParticleSpawnType type) {
        try {
            switch (type) {
                case EMITTER, EMITTER_BOUND ->
                        this.getEmitter().particleEffect(effect, ParticleEvent.ParticleSpawnType.PARTICLE);
                case PARTICLE ->
                        this.level.addParticle(BedrockParticle.getOptions(effect), this.x, this.y, this.z, 0, 0, 0);
                case PARTICLE_WITH_VELOCITY -> {
                    double xd;
                    double yd;
                    double zd;
                    if (this.physics != null) {
                        Vector3dc velocity = this.physics.getVelocity();
                        xd = velocity.x();
                        yd = velocity.y();
                        zd = velocity.z();
                    } else {
                        xd = 0;
                        yd = 0;
                        zd = 0;
                    }
                    this.level.addParticle(BedrockParticle.getOptions(effect), this.x, this.y, this.z, xd, yd, zd);
                }
            }
        } catch (Exception e) {
            LOGGER.error(this.getPrefix().getString() + "Failed to spawn particle: {}", effect, e);
        }
    }

    @Override
    public void soundEffect(String sound) {
        ResourceLocation soundId = ResourceLocation.tryParse(sound);
        if (soundId == null) {
            LOGGER.error("Invalid sound id: {}", sound);
            return;
        }
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(soundId, SoundSource.AMBIENT, 1.0F, 1.0F, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.LINEAR, this.x, this.y, this.z, false));
    }

    @Override
    public void log(String message) {
        Minecraft.getInstance().gui.getChat().addMessage(Component.empty().append(this.getPrefix()).append(message));
    }

    @Override
    public Random getRandom() {
        return this.random;
    }

    @Override
    public Vector3dc position() {
        return this.pos.set(this.x, this.y, this.z);
    }

    @Override
    public BlockPos blockPosition() {
        return this.blockPos;
    }

    @Override
    public float roll() {
        return this.roll;
    }

    @Override
    public Vector3dc position(float partialTicks) {
        return this.renderPos.set(Mth.lerp(partialTicks, this.xo, this.x), Mth.lerp(partialTicks, this.yo, this.y), Mth.lerp(partialTicks, this.zo, this.z));
    }

    @Override
    public float roll(float partialTicks) {
        return Mth.lerp(partialTicks, this.oRoll, this.roll);
    }

    @Override
    public void expire() {
        this.remove();
    }

    @Override
    public boolean isExpired() {
        return !this.isAlive();
    }

    @Override
    public ResourceLocation getName() {
        return this.name;
    }

    @Override
    public ClientLevel getLevel() {
        return this.level;
    }

    @Override
    public @Nullable BedrockParticlePhysics getPhysics() {
        return this.physics;
    }

    @Override
    public float getParticleAge() {
        return this.renderAge.getValue();
    }

    @Override
    public float getParticleLifetime() {
        return this.lifetime.getValue();
    }

    @Override
    public void setLifetime(float time) {
        this.lifetime.setValue(time);
    }

    @Override
    public void setX(double x) {
        this.x = x;
        this.blockPos.set(this.x, this.y, this.z);
    }

    @Override
    public void setY(double y) {
        this.y = y;
        this.blockPos.set(this.x, this.y, this.z);
    }

    @Override
    public void setZ(double z) {
        this.z = z;
        this.blockPos.set(this.x, this.y, this.z);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.setBoundingBox(new AABB(x - this.radius, y, z - this.radius, x + this.radius, y + this.radius * 2, z + this.radius));
        this.blockPos.set(x, y, z);
    }

    @Override
    public void setRoll(float roll) {
        if (this.oRoll == 0 && this.roll == 0) {
            this.oRoll = roll;
        }
        this.roll = roll;
    }

    @Override
    public MolangEnvironment getEnvironment() {
        return this.environment;
    }
}
