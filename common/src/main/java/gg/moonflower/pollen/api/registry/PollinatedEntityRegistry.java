package gg.moonflower.pollen.api.registry;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A specialized registry for entities and Ai types.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class PollinatedEntityRegistry extends WrapperPollinatedRegistry<EntityType<?>> {

    private final PollinatedRegistry<MemoryModuleType<?>> memoryModuleTypeRegistry;
    private final PollinatedRegistry<SensorType<?>> sensorTypeRegistry;
    private final PollinatedRegistry<Schedule> scheduleRegistry;
    private final PollinatedRegistry<Activity> activityRegistry;

    PollinatedEntityRegistry(PollinatedRegistry<EntityType<?>> entityRegistry) {
        super(entityRegistry);
        this.memoryModuleTypeRegistry = create(Registry.MEMORY_MODULE_TYPE, entityRegistry.getModId());
        this.sensorTypeRegistry = create(Registry.SENSOR_TYPE, entityRegistry.getModId());
        this.scheduleRegistry = create(Registry.SCHEDULE, entityRegistry.getModId());
        this.activityRegistry = create(Registry.ACTIVITY, entityRegistry.getModId());
    }

    @Override
    protected void onRegister(Platform mod) {
        super.onRegister(mod);
        this.memoryModuleTypeRegistry.onRegister(mod);
        this.sensorTypeRegistry.onRegister(mod);
        this.scheduleRegistry.onRegister(mod);
        this.activityRegistry.onRegister(mod);
    }

    /**
     * Registers a memory module type without a codec defined.
     *
     * @param id  The id of the module
     * @param <R> The type of object the module is for
     * @return A new memory module type
     */
    public <R> Supplier<MemoryModuleType<R>> registerMemoryModuleType(String id) {
        return this.registerMemoryModuleType(id, null);
    }

    /**
     * Registers a memory module type.
     *
     * @param id    The id of the module
     * @param codec The codec for the module or <code>null</code> for none
     * @param <R>   The type of object the module is for
     * @return A new memory module type
     */
    public <R> Supplier<MemoryModuleType<R>> registerMemoryModuleType(String id, @Nullable Codec<R> codec) {
        return this.memoryModuleTypeRegistry.register(id, () -> new MemoryModuleType<>(Optional.ofNullable(codec)));
    }

    /**
     * Registers a new sensor type.
     *
     * @param id       The id of the sensor
     * @param supplier The generator for new sensor values
     * @param <R>      The type of sensor to generate
     * @return A new sensor type
     */
    public <R extends Sensor<?>> Supplier<SensorType<R>> registerSensorType(String id, Supplier<R> supplier) {
        return this.sensorTypeRegistry.register(id, () -> new SensorType<>(supplier));
    }

    /**
     * Registers a new schedule.
     *
     * @param id      The id of the schedule
     * @param builder The consumer for adding properties to the schedule
     * @return A new schedule
     */
    public Supplier<Schedule> registerSchedule(String id, Consumer<ScheduleBuilder> builder) {
        return this.scheduleRegistry.register(id, () -> {
            Schedule schedule = new Schedule();
            builder.accept(new ScheduleBuilder(schedule));
            return schedule;
        });
    }

    /**
     * Registers a new activity.
     *
     * @param id The id of the activity
     * @return A new activity
     */
    public Supplier<Activity> registerActivity(String id) {
        return this.activityRegistry.register(id, () -> new Activity(this.activityRegistry.getModId() + ":" + id));
    }

    /**
     * @return The registry used to add memory module types. This is automatically registered by this registry
     */
    public PollinatedRegistry<MemoryModuleType<?>> getMemoryModuleTypeRegistry() {
        return memoryModuleTypeRegistry;
    }

    /**
     * @return The registry used to add sensor types. This is automatically registered by this registry
     */
    public PollinatedRegistry<SensorType<?>> getSensorTypeRegistry() {
        return sensorTypeRegistry;
    }


    /**
     * @return The registry used to add schedules. This is automatically registered by this registry
     */
    public PollinatedRegistry<Schedule> getScheduleRegistry() {
        return scheduleRegistry;
    }


    /**
     * @return The registry used to add activities. This is automatically registered by this registry
     */
    public PollinatedRegistry<Activity> getActivityRegistry() {
        return activityRegistry;
    }
}
