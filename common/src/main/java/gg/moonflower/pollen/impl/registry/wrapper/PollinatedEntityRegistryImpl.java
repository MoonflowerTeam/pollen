package gg.moonflower.pollen.impl.registry.wrapper;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import gg.moonflower.pollen.api.registry.wrapper.v1.PollinatedEntityRegistry;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@ApiStatus.Internal
public class PollinatedEntityRegistryImpl extends PollinatedRegistryImpl<EntityType<?>> implements PollinatedEntityRegistry {

    private final DeferredRegister<MemoryModuleType<?>> memoryModuleTypeRegistry;
    private final DeferredRegister<SensorType<?>> sensorTypeRegistry;
    private final DeferredRegister<Schedule> scheduleRegistry;
    private final DeferredRegister<Activity> activityRegistry;

    public PollinatedEntityRegistryImpl(DeferredRegister<EntityType<?>> entityRegistry) {
        super(entityRegistry);
        this.memoryModuleTypeRegistry = DeferredRegister.create(this.getModId(), Registry.MEMORY_MODULE_TYPE_REGISTRY);
        this.sensorTypeRegistry = DeferredRegister.create(this.getModId(), Registry.SENSOR_TYPE_REGISTRY);
        this.scheduleRegistry = DeferredRegister.create(this.getModId(), Registry.SCHEDULE_REGISTRY);
        this.activityRegistry = DeferredRegister.create(this.getModId(), Registry.ACTIVITY_REGISTRY);
    }


    @Override
    public void register() {
        super.register();
        this.memoryModuleTypeRegistry.register();
        this.sensorTypeRegistry.register();
        this.scheduleRegistry.register();
        this.activityRegistry.register();
    }

    @Override
    public <R> RegistrySupplier<MemoryModuleType<R>> registerMemoryModuleType(String id) {
        return this.registerMemoryModuleType(id, null);
    }

    @Override
    public <R> RegistrySupplier<MemoryModuleType<R>> registerMemoryModuleType(String id, @Nullable Codec<R> codec) {
        return this.memoryModuleTypeRegistry.register(id, () -> new MemoryModuleType<>(Optional.ofNullable(codec)));
    }

    @Override
    public <R extends Sensor<?>> RegistrySupplier<SensorType<R>> registerSensorType(String id, Supplier<R> supplier) {
        return this.sensorTypeRegistry.register(id, () -> new SensorType<>(supplier));
    }

    @Override
    public RegistrySupplier<Schedule> registerSchedule(String id, Consumer<ScheduleBuilder> builder) {
        return this.scheduleRegistry.register(id, () -> {
            Schedule schedule = new Schedule();
            builder.accept(new ScheduleBuilder(schedule));
            return schedule;
        });
    }

    @Override
    public RegistrySupplier<Activity> registerActivity(String id) {
        return this.activityRegistry.register(id, () -> new Activity(this.getModId() + ":" + id));
    }

    @Override
    public DeferredRegister<MemoryModuleType<?>> getMemoryModuleTypeRegistry() {
        return memoryModuleTypeRegistry;
    }

    @Override
    public DeferredRegister<SensorType<?>> getSensorTypeRegistry() {
        return sensorTypeRegistry;
    }

    @Override
    public DeferredRegister<Schedule> getScheduleRegistry() {
        return scheduleRegistry;
    }

    @Override
    public DeferredRegister<Activity> getActivityRegistry() {
        return activityRegistry;
    }
}
