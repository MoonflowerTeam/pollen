package gg.moonflower.pollen.api.registry.wrapper.v1;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import gg.moonflower.pollen.impl.registry.wrapper.PollinatedEntityRegistryImpl;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A specialized registry for entities and Ai types.
 *
 * @author Ocelot, Jackson
 * @since 2.0.0
 */
public interface PollinatedEntityRegistry extends PollinatedRegistry<EntityType<?>> {

    static PollinatedEntityRegistry create(String modId) {
        return new PollinatedEntityRegistryImpl(DeferredRegister.create(modId, Registry.ENTITY_TYPE_REGISTRY));
    }

    /**
     * Registers a memory module type without a codec defined.
     *
     * @param id  The id of the module
     * @param <R> The type of object the module is for
     * @return A new memory module type
     */
    <R> RegistrySupplier<MemoryModuleType<R>> registerMemoryModuleType(String id);

    /**
     * Registers a memory module type.
     *
     * @param id    The id of the module
     * @param codec The codec for the module or <code>null</code> for none
     * @param <R>   The type of object the module is for
     * @return A new memory module type
     */
    <R> RegistrySupplier<MemoryModuleType<R>> registerMemoryModuleType(String id, @Nullable Codec<R> codec);

    /**
     * Registers a new sensor type.
     *
     * @param id       The id of the sensor
     * @param supplier The generator for new sensor values
     * @param <R>      The type of sensor to generate
     * @return A new sensor type
     */
    <R extends Sensor<?>> RegistrySupplier<SensorType<R>> registerSensorType(String id, Supplier<R> supplier);

    /**
     * Registers a new schedule.
     *
     * @param id      The id of the schedule
     * @param builder The consumer for adding properties to the schedule
     * @return A new schedule
     */
    RegistrySupplier<Schedule> registerSchedule(String id, Consumer<ScheduleBuilder> builder);

    /**
     * Registers a new activity.
     *
     * @param id The id of the activity
     * @return A new activity
     */
    RegistrySupplier<Activity> registerActivity(String id);

    /**
     * @return The registry used to add memory module types. This is automatically registered by this registry
     */
    DeferredRegister<MemoryModuleType<?>> getMemoryModuleTypeRegistry();

    /**
     * @return The registry used to add sensor types. This is automatically registered by this registry
     */
    DeferredRegister<SensorType<?>> getSensorTypeRegistry();


    /**
     * @return The registry used to add schedules. This is automatically registered by this registry
     */
   DeferredRegister<Schedule> getScheduleRegistry();

    /**
     * @return The registry used to add activities. This is automatically registered by this registry
     */
    DeferredRegister<Activity> getActivityRegistry();
}
