package gg.moonflower.pollen.pinwheel.core.client.particle;

import com.google.common.base.Suppliers;
import com.mojang.brigadier.StringReader;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import gg.moonflower.pollen.api.particle.CustomParticleOption;
import gg.moonflower.pollen.api.particle.PollenParticles;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticle;
import gg.moonflower.pollen.pinwheel.api.client.particle.CustomParticleManager;
import gg.moonflower.pollen.pinwheel.api.common.particle.ParticleData;
import gg.moonflower.pollen.pinwheel.api.common.particle.component.*;
import gg.moonflower.pollen.pinwheel.api.common.particle.event.ParticleEvent;
import gg.moonflower.pollen.pinwheel.core.client.ProfilingMolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangExpression;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import io.github.ocelot.molangcompiler.api.bridge.MolangVariable;
import io.github.ocelot.molangcompiler.api.bridge.MolangVariableProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.commands.arguments.ParticleArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public abstract class CustomParticleImpl extends Particle implements CustomParticle, MolangVariableProvider {

    protected static final Logger LOGGER = LogUtils.getLogger();
    protected static final double MAXIMUM_COLLISION_VELOCITY_SQUARED = Mth.square(100.0);

    protected final ResourceLocation name;
    protected final ParticleData data;

    protected final MolangVariable renderAge = MolangVariable.create();
    protected final MolangVariable lifetime = MolangVariable.create();
    protected final MolangVariable random1;
    protected final MolangVariable random2;
    protected final MolangVariable random3;
    protected final MolangVariable random4;

    protected final Set<CustomParticleListener> listeners = new HashSet<>();
    protected final Map<CustomParticleComponentType<?>, CustomParticleComponent> components = new HashMap<>();
    protected final Map<CustomParticleComponentType<?>, CustomParticleComponent> childComponents = new HashMap<>();
    protected final Map<String, Pair<ParticleData.Curve, MolangVariable>> variables = new HashMap<>();
    private final Set<CustomParticleTickComponent> tickComponents;
    private final Set<CustomParticlePhysicsTickComponent> physicsTickComponents;
    private final Supplier<MolangRuntime.Builder> builder;
    private final Supplier<MolangEnvironment> runtime;
    private final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

    protected Vec3 direction = Vec3.ZERO;
    protected float speed = 0;
    protected Vec3 acceleration = Vec3.ZERO;
    protected float rotationVelocity = 0;
    protected float rotationAcceleration = 0;
    private boolean disableMovement;

    protected CustomParticleImpl(ClientLevel clientLevel, double x, double y, double z, ResourceLocation name, Function<CustomParticleImpl, MolangRuntime.Builder> runtimeFactory) {
        super(clientLevel, x, y, z);
        this.name = name;
        this.data = CustomParticleManager.getParticle(this.name);
        this.random1 = MolangVariable.create(this.random.nextFloat());
        this.random2 = MolangVariable.create(this.random.nextFloat());
        this.random3 = MolangVariable.create(this.random.nextFloat());
        this.random4 = MolangVariable.create(this.random.nextFloat());
        this.tickComponents = new HashSet<>();
        this.physicsTickComponents = new HashSet<>();
        this.builder = Suppliers.memoize(() -> runtimeFactory.apply(this));
        this.runtime = Suppliers.memoize(() -> new ProfilingMolangEnvironment(this.getRuntimeBuilder().create(), clientLevel.getProfilerSupplier()));
        this.hasPhysics = false;
        this.age = -1;
    }

    protected void addComponent(CustomParticleComponentType<?> type, CustomParticleComponent instance) {
        if (type.isValid(this)) {
            this.components.put(type, instance);
            if (instance instanceof CustomParticleListener listener) {
                this.addListener(listener);
            }
            if (instance instanceof CustomParticleTickComponent tickComponent) {
                this.tickComponents.add(tickComponent);
            }
            if (instance instanceof CustomParticlePhysicsTickComponent tickComponent) {
                this.physicsTickComponents.add(tickComponent);
            }
        }
    }

    @Override
    public void runEvent(String name) {
        if (this.data.events().containsKey(name))
            this.data.events().get(name).execute(this);
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
    public float getParticleAge() {
        return this.renderAge.getValue();
    }

    @Override
    public float getParticleLifetime() {
        return this.lifetime.getValue();
    }

    @Override
    public void addListener(CustomParticleListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(CustomParticleListener listener) {
        this.listeners.remove(listener);
    }

    protected void evaluateCurves() {
        if (this.variables.isEmpty())
            return;
        MolangEnvironment runtime = this.getRuntime();
        ProfilerFiller profiler = this.level.getProfiler();
        profiler.push("evaluateCurves");
        this.variables.forEach((variable, pair) -> pair.getSecond().setValue(evaluateCurve(runtime, pair.getFirst())));
        profiler.pop();
    }

    @Override
    public void tick() {
        ProfilerFiller profiler = this.level.getProfiler();
        profiler.push("pollen");

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.oRoll = this.roll;
        this.blockPos.set(this.x, this.y, this.z);

        // Init
        this.getRuntime(); // Load runtime
        if (this.age == -1) {
            this.age = 0;
            profiler.push("init");
            this.listeners.forEach(listener -> listener.onCreate(this));
            profiler.pop();
        }

        this.evaluateCurves();

        // Tick components
        profiler.push("components");
        this.tickComponents.forEach(component -> component.tick(this));
        profiler.popPush("physics");

        // Tick motion
        if (!this.disableMovement) { // Stop moving particles if they collide and effectively stop because of it
            profiler.push("components");
            this.physicsTickComponents.forEach(component -> component.physicsTick(this));
            profiler.popPush("move");
            if (this.acceleration.lengthSqr() > 1.0E-7) {
                this.setVelocity(this.getVelocity().add(this.acceleration));
            }
            if (this.speed * this.speed > 1.0E-7) {
                Vec3 direction = this.getDirection();
                this.move(direction.x() * this.speed, direction.y() * this.speed, direction.z() * this.speed);
            }
            profiler.pop();
        }
        profiler.pop();

        // Tick age
        this.age++;

        profiler.pop();
    }

    @Override
    public void move(double dx, double dy, double dz) {
        double g = dx;
        double h = dy;
        double i = dz;
        if (this.hasPhysics && (dx != 0.0 || dy != 0.0 || dz != 0.0) && dx * dx + dy * dy + dz * dz < MAXIMUM_COLLISION_VELOCITY_SQUARED) {
            Vec3 vec3 = Entity.collideBoundingBox(null, new Vec3(dx, dy, dz), this.getBoundingBox(), this.level, List.of());
            dx = vec3.x;
            dy = vec3.y;
            dz = vec3.z;
        }

        if (dx != 0.0 || dy != 0.0 || dz != 0.0) {
            this.setBoundingBox(this.getBoundingBox().move(dx, dy, dz));
            this.setLocationFromBoundingbox();
            for (CustomParticleListener listener : this.listeners)
                listener.onMove(this, dx, dy, dz);
        }

        if (this.hasPhysics) {
            boolean xCollision = Math.abs(g) >= 1.0E-5F && Math.abs(dx) < 1.0E-5F;
            boolean yCollision = Math.abs(h) >= 1.0E-5F && Math.abs(dy) < 1.0E-5F;
            boolean zCollision = Math.abs(i) >= 1.0E-5F && Math.abs(dz) < 1.0E-5F;
            this.onGround = h != dy && h < 0.0;

            if (xCollision || yCollision || zCollision) {
                this.listeners.forEach(listener -> listener.onCollide(this, xCollision, yCollision, zCollision));
                if (this.onGround && this.speed * this.speed <= 1.0E-7) {
                    this.disableMovement = true;
                }
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
        this.listeners.forEach(listener -> listener.onExpire(this));
    }

    @Override
    public void addMolangVariables(Context context) {
        this.variables.forEach((name, pair) -> context.addVariable(name, pair.getSecond()));
    }

    @Override
    public void soundEffect(ResourceLocation sound) {
        Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(sound, SoundSource.AMBIENT, 1.0F, 1.0F, false, 0, SoundInstance.Attenuation.LINEAR, this.x, this.y, this.z, false));
    }

    @Override
    public void expression(MolangExpression expression) {
        expression.safeResolve(this.getRuntime());
    }

    @Override
    public void log(String message) {
        Minecraft.getInstance().gui.getChat().addMessage(new TextComponent("").append(this.getPrefix()).append(message));
    }

    @Override
    public Random getRandom() {
        return random;
    }

    @Override
    public boolean hasCollision() {
        return this.hasPhysics;
    }

    @Override
    public float getCollisionRadius() {
        return Math.max(this.bbWidth, this.bbHeight);
    }

    @Override
    public void setLifetime(float time) {
        this.lifetime.setValue(time);
    }

    @Nullable
    public ParticleOptions getOptions(String effect) {
        try {
            ResourceLocation id = new ResourceLocation(effect);
            if (CustomParticleManager.hasParticle(id)) {
                return new CustomParticleOption(PollenParticles.CUSTOM.get(), id);
            } else {
                return ParticleArgument.readParticle(new StringReader(effect));
            }
        } catch (Exception e) {
            LOGGER.error(this.getPrefix().getString() + "Failed to spawn particle: {}", effect, e);
        }
        return null;
    }

    protected Component getPrefix() {
        return new TextComponent("").append(new TextComponent("[" + this.name + "]").withStyle(ChatFormatting.AQUA)).append(" ");
    }

    private static float evaluateCurve(MolangEnvironment runtime, ParticleData.Curve curve) {
        float horizontalRange = curve.horizontalRange().safeResolve(runtime);
        if (horizontalRange == 0)
            return 1.0F;
        float input = curve.input().safeResolve(runtime) / horizontalRange;

        ParticleData.CurveNode[] nodes = curve.nodes();
        int index = getIndex(curve, input);

        switch (curve.type()) {
            case LINEAR -> {
                ParticleData.CurveNode current = nodes[index];
                ParticleData.CurveNode next = index + 1 >= nodes.length ? current : nodes[index + 1];

                float a = current.getValue().safeResolve(runtime);
                float b = next.getValue().safeResolve(runtime);
                float progress = (input - current.getTime()) / (next.getTime() - current.getTime());

                return Mth.lerp(progress, a, b);
            }
            case BEZIER -> {
                float a = nodes[0].getValue().safeResolve(runtime);
                float b = nodes[1].getValue().safeResolve(runtime);
                float c = nodes[2].getValue().safeResolve(runtime);
                float d = nodes[3].getValue().safeResolve(runtime);

                return bezier(a, b, c, d, input);
            }
            case BEZIER_CHAIN -> {
                ParticleData.BezierChainCurveNode current = (ParticleData.BezierChainCurveNode) nodes[index];
                if (index + 1 >= nodes.length)
                    return current.getRightValue().safeResolve(runtime);

                ParticleData.BezierChainCurveNode next = (ParticleData.BezierChainCurveNode) nodes[index + 1];
                float step = input - current.getTime() + next.getTime() / 3F;
                float a = current.getRightValue().safeResolve(runtime);
                float b = a + step * current.getRightSlope().safeResolve(runtime);
                float d = next.getLeftValue().safeResolve(runtime);
                float c = d - step * next.getLeftSlope().safeResolve(runtime);
                float progress = (input - current.getTime()) / (next.getTime() - current.getTime());

                return bezier(a, b, c, d, progress);
            }
            case CATMULL_ROM -> {
                try {
                    ParticleData.CurveNode last = nodes[index - 1];
                    ParticleData.CurveNode from = nodes[index];
                    ParticleData.CurveNode to = nodes[index + 1];
                    ParticleData.CurveNode after = nodes[index + 2];

                    float a = last.getValue().safeResolve(runtime);
                    float b = from.getValue().safeResolve(runtime);
                    float c = to.getValue().safeResolve(runtime);
                    float d = after.getValue().safeResolve(runtime);
                    float nextTime = to.getTime();
                    float progress = (input - from.getTime()) / (nextTime - from.getTime());

                    return catmullRom(a, b, c, d, Mth.clamp(progress, 0, 1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return input;
    }

    private static int getIndex(ParticleData.Curve curve, float input) {
        int best = 0;
        ParticleData.CurveNode[] nodes = curve.nodes();
        int offset = curve.type() == ParticleData.CurveType.CATMULL_ROM ? 1 : 0;
        for (int i = offset; i < nodes.length - offset * 2; i++) {
            ParticleData.CurveNode node = nodes[i];
            if (node.getTime() > input) {
                break;
            }

            best = i;
        }

        return best;
    }

    private static float bezier(float p0, float p1, float p2, float p3, float t) {
        return (1 - t) * (1 - t) * (1 - t) * p0 + 3 * (1 - t) * (1 - t) * t * p1 + 3 * (1 - t) * t * t * p2 + t * t * t * p3;
    }

    private static float catmullRom(float p0, float p1, float p2, float p3, float t) {
        return 0.5F * ((2 * p1) + (-p0 + p2) * t + (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t + (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t);
    }

    @Override
    public ResourceLocation getName() {
        return name;
    }

    @Override
    public MolangEnvironment getRuntime() {
        return this.runtime.get();
    }

    protected MolangRuntime.Builder getRuntimeBuilder() {
        return this.builder.get();
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public BlockPos blockPos() {
        return blockPos;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    @Override
    public double x(float partialTicks) {
        return Mth.lerp(partialTicks, this.xo, this.x);
    }

    @Override
    public double y(float partialTicks) {
        return Mth.lerp(partialTicks, this.yo, this.y);
    }

    @Override
    public double z(float partialTicks) {
        return Mth.lerp(partialTicks, this.zo, this.z);
    }

    @Override
    public float rotation() {
        return roll;
    }

    @Override
    public void setX(double x) {
        this.setPos(x, this.y, this.z);
    }

    @Override
    public void setY(double y) {
        this.setPos(this.x, y, this.z);
    }

    @Override
    public void setZ(double z) {
        this.setPos(this.x, this.y, z);
    }

    @Override
    public void setPosition(double x, double y, double z) {
        this.setPos(x, y, z);
    }

    @Override
    public void setRotation(float rotation) {
        if (this.oRoll == 0 && this.roll == 0)
            this.oRoll = rotation;
        this.roll = rotation;
    }

    @Override
    public Vec3 getDirection() {
        return direction;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public Vec3 getAcceleration() {
        return acceleration;
    }

    @Override
    public float getRotationVelocity() {
        return rotationVelocity;
    }

    @Override
    public float getRotationAcceleration() {
        return rotationAcceleration;
    }

    @Override
    public void setDirection(Vec3 direction) {
        this.direction = direction.normalize();
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = Math.max(0, speed);
    }

    @Override
    public void setAcceleration(Vec3 acceleration) {
        this.acceleration = acceleration;
    }

    @Override
    public void setRotationVelocity(float velocity) {
        this.rotationVelocity = velocity;
    }

    @Override
    public void setRotationAcceleration(float acceleration) {
        this.rotationAcceleration = acceleration;
    }

    @Override
    public void setCollision(boolean enabled) {
        this.hasPhysics = enabled;
    }

    @Override
    public void setCollisionRadius(float radius) {
        this.setSize(radius * 2, radius * 2);
    }
}
