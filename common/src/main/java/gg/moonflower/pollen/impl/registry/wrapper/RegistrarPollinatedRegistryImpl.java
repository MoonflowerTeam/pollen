package gg.moonflower.pollen.impl.registry.wrapper;

import com.google.common.base.Suppliers;
import dev.architectury.registry.registries.*;
import gg.moonflower.pollen.api.registry.wrapper.v1.PollinatedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Creates a new registry using a new registrar from builder.
 *
 * @author Ocelot
 */
@ApiStatus.Internal
public class RegistrarPollinatedRegistryImpl<T> implements PollinatedRegistry<T> {

    private final Supplier<Registries> registries;
    private final Supplier<Registrar<T>> registrar;
    private final ResourceKey<Registry<T>> key;

    public RegistrarPollinatedRegistryImpl(ResourceKey<Registry<T>> key, @Nullable Consumer<RegistrarBuilder<T>> consumer) {
        this.registries = Suppliers.memoize(() -> Registries.get(this.getModId()));
        this.registrar = Suppliers.memoize(() -> {
            RegistrarBuilder<T> builder = this.registries.get().builder(key.location());
            if (consumer != null) {
                consumer.accept(builder);
            }
            return builder.build();
        });
        this.key = key;
    }

    @Override
    public <R extends T> RegistrySupplier<R> register(String id, Supplier<R> supplier) {
        return register(new ResourceLocation(this.getModId(), id), supplier);
    }

    @Override
    public <R extends T> RegistrySupplier<R> register(ResourceLocation id, Supplier<R> supplier) {
        return this.getRegistrar().register(id, supplier);
    }

    @Override
    public void register() {
    }

    @Override
    public Iterator<RegistrySupplier<T>> iterator() {
        Registrar<T> registrar = this.getRegistrar();
        Iterator<T> iterator = registrar.iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public RegistrySupplier<T> next() {
                return registrar.wrap(iterator.next());
            }
        };
    }

    @Override
    public Registries getRegistries() {
        return this.registries.get();
    }

    @Override
    public Registrar<T> getRegistrar() {
        return this.registrar.get();
    }

    @Override
    public String getModId() {
        return this.key.location().getNamespace();
    }
}
