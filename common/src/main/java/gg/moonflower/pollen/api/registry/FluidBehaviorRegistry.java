package gg.moonflower.pollen.api.registry;

import gg.moonflower.pollen.api.fluid.PollenFluidBehavior;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class FluidBehaviorRegistry {

    private static final Map<TagKey<Fluid>, PollenFluidBehavior> FLUID_BEHAVIOR = new ConcurrentHashMap<>();

    private FluidBehaviorRegistry() {
    }

    public static void register(TagKey<Fluid> tag, PollenFluidBehavior behavior) {
        FLUID_BEHAVIOR.put(tag, behavior);
    }

    public static Stream<PollenFluidBehavior> get(Predicate<TagKey<Fluid>> filter) {
        return FLUID_BEHAVIOR.entrySet().stream().filter(entry -> filter.test(entry.getKey())).map(Map.Entry::getValue);
    }

    @Nullable
    public static PollenFluidBehavior get(TagKey<Fluid> tag) {
        return FLUID_BEHAVIOR.get(tag);
    }

    public static Set<TagKey<Fluid>> getFluids() {
        return FLUID_BEHAVIOR.keySet();
    }

    @ApiStatus.Internal
    public static boolean doFluidPushing(TagKey<Fluid> tag, Entity entity) {
        if (entity.getVehicle() instanceof Boat)
            return false;

        PollenFluidBehavior behavior = FluidBehaviorRegistry.get(tag);
        if (behavior == null)
            return false;
        if (entity.updateFluidHeightAndDoFluidPushing(tag, behavior.getMotionScale(entity))) {
            if (behavior.canExtinguishFire(entity))
                entity.clearFire();
            if (behavior.negatesFallDamage(entity))
                entity.fallDistance = 0;
            return true;
        }
        return false;
    }
}
