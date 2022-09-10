package gg.moonflower.pollen.api.fluid.v1;

import gg.moonflower.pollen.impl.fluid.FluidBehaviorRegistryImpl;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Registers behavior for fluid tags.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface FluidBehaviorRegistry {

    /**
     * Registers behavior for the specified tag.
     *
     * @param tag      The tag to bind behavior to
     * @param behavior The behavior instance
     */
    static void register(TagKey<Fluid> tag, PollenFluidBehavior behavior) {
        FluidBehaviorRegistryImpl.register(tag, behavior);
    }

    /**
     * Retrieves behaviors that match the specified filter.
     *
     * @param filter The filter for choosing what behaviors to retrieve
     * @return A stream of all behaviors matching
     */
    static Stream<PollenFluidBehavior> get(Predicate<TagKey<Fluid>> filter) {
        return FluidBehaviorRegistryImpl.get(filter);
    }

    /**
     * Retrieves behavior for the specified tag.
     *
     * @param tag The fluid tag to get behavior for
     * @return The behavior for that fluid tag or <code>null</code>
     */
    @Nullable
    static PollenFluidBehavior get(TagKey<Fluid> tag) {
        return FluidBehaviorRegistryImpl.get(tag);
    }

    /**
     * @return All fluid tags with behavior
     */
    static Set<TagKey<Fluid>> getFluids() {
        return FluidBehaviorRegistryImpl.getFluids();
    }
}
