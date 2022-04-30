package gg.moonflower.pollen.api.registry;

import gg.moonflower.pollen.api.fluid.PollenFluidBehavior;
import net.minecraft.tags.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registers behavior for fluid tags.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class FluidBehaviorRegistry {

    private static final Map<Tag<Fluid>, PollenFluidBehavior> FLUID_BEHAVIOR = new ConcurrentHashMap<>();

    private FluidBehaviorRegistry() {
    }

    /**
     * Registers behavior for the specified tag.
     *
     * @param tag      The tag to bind behavior to
     * @param behavior The behavior instance
     */
    public static void register(Tag<Fluid> tag, PollenFluidBehavior behavior) {
        FLUID_BEHAVIOR.put(tag, behavior);
    }

    /**
     * Retrieves behavior for the specified tag.
     *
     * @param tag The fluid tag to get behavior for
     * @return The behavior for that fluid tag or <code>null</code>
     */
    @Nullable
    public static PollenFluidBehavior get(Tag<Fluid> tag) {
        return FLUID_BEHAVIOR.get(tag);
    }

    /**
     * @return All fluid tags with behavior
     */
    public static Set<Tag<Fluid>> getFluids() {
        return FLUID_BEHAVIOR.keySet();
    }

    @ApiStatus.Internal
    public static boolean doFluidPushing(Tag<Fluid> tag, Entity entity) {
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
