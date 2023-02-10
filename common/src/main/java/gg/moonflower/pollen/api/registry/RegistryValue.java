package gg.moonflower.pollen.api.registry;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * A reference to an object registered by a {@link PollinatedRegistry}.
 *
 * @param <T> The object type
 * @author ebo2022
 * @since
 */
public interface RegistryValue<T> extends Supplier<T> {

    /**
     * @return The held value. This may throw an exception if called too early
     */
    @Override
    T get();

    /**
     * @return An optional of the held value based on whether it is present
     */
    default Optional<T> getOptional() {
        return this.isPresent() ? Optional.of(this.get()) : Optional.empty();
    }

    /**
     * @return An optional of the held object as a vanilla {@link Holder}
     */
    Optional<Holder<T>> getHolder();

    /**
     * @return Whether the value is present
     */
    boolean isPresent();

    /**
     * @return The name of the held value
     */
    ResourceLocation getId();

    /**
     * @return A {@link ResourceKey} representing the held value
     */
    ResourceKey<T> getKey();
}
