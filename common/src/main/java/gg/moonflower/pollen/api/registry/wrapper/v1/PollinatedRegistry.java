package gg.moonflower.pollen.api.registry.wrapper.v1;

import dev.architectury.registry.registries.*;
import gg.moonflower.pollen.impl.registry.wrapper.RegistrarPollinatedRegistryImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Wraps an existing {@link DeferredRegister} to add extra functionality.
 *
 * @param <T> The object type
 * @author Jackson
 * @since 2.0.0
 */
public interface PollinatedRegistry<T> {

    <R extends T> RegistrySupplier<R> register(String id, Supplier<R> supplier);

    <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<R> supplier);

    void register();

    Iterator<RegistrySupplier<T>> iterator();

    Registries getRegistries();

    Registrar<T> getRegistrar();

    String getModId();

    /**
     * Creates a new pollinated registry backed by a new registrar instance.
     *
     * @param registry The key of the registry to create
     * @param <T>      The type of values to register
     * @return A new registry
     */
    static <T> PollinatedRegistry<T> create(ResourceKey<Registry<T>> registry) {
        return create(registry, null);
    }

    /**
     * Creates a new pollinated registry backed by a new registrar instance.
     *
     * @param registry The key of the registry to create
     * @param builder  A consumer to modify the registrar or <code>null</code> to use defaults
     * @param <T>      The type of values to register
     * @return A new registry
     */
    static <T> PollinatedRegistry<T> create(ResourceKey<Registry<T>> registry, @Nullable Consumer<RegistrarBuilder<T>> builder) {
        return new RegistrarPollinatedRegistryImpl<>(registry, builder);
    }
}
