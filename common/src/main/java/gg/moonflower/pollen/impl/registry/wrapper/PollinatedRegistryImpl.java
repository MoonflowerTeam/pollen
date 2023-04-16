package gg.moonflower.pollen.impl.registry.wrapper;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import gg.moonflower.pollen.api.registry.wrapper.v1.PollinatedRegistry;
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
public class PollinatedRegistryImpl<T> implements PollinatedRegistry<T> {

    private final DeferredRegister<T> parent;
    private final String modId;

    protected PollinatedRegistryImpl(DeferredRegister<T> parent) {
        this.parent = parent;
        this.modId = this.parent.getRegistries().getModId();
    }

    @Override
    public <R extends T> RegistrySupplier<R> register(String id, Supplier<R> supplier) {
        return this.parent.register(id, supplier);
    }

    @Override
    public <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<R> supplier) {
        return this.parent.register(id, supplier);
    }

    @Override
    public void register() {
        this.parent.register();
    }

    @Override
    public Iterator<RegistrySupplier<T>> iterator() {
        return this.parent.iterator();
    }

    @Override
    public Registries getRegistries() {
        return this.parent.getRegistries();
    }

    @Override
    public Registrar<T> getRegistrar() {
        return this.parent.getRegistrar();
    }

    @Override
    public String getModId() {
        return modId;
    }
}
