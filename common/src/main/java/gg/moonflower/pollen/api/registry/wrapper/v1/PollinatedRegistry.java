package gg.moonflower.pollen.api.registry.wrapper.v1;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;

import java.util.Iterator;
import java.util.function.Supplier;

/**
 * Wraps an existing {@link DeferredRegister} to add extra functionality.
 *
 * @param <T> The object type
 * @author Jackson
 * @since 2.0.0
 */
public interface PollinatedRegistry<T> {

    <R extends T> RegistrySupplier<R> register(String id, Supplier<? extends R> supplier);

    <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<? extends R> supplier);

    void register();

    Iterator<RegistrySupplier<T>> iterator();

    Registries getRegistries();

    Registrar<T> getRegistrar();

    String getModId();
}
