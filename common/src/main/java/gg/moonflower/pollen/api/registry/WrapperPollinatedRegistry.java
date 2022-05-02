package gg.moonflower.pollen.api.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Wraps an existing {@link PollinatedRegistry} to add extra functionality.
 *
 * @param <T> The object type
 * @author Ocelot
 * @since 1.0.0
 */
public class WrapperPollinatedRegistry<T> extends PollinatedRegistry<T> {

    private final PollinatedRegistry<T> parent;

    protected WrapperPollinatedRegistry(PollinatedRegistry<T> parent) {
        super(parent.getModId());
        this.parent = parent;
    }

    @Override
    public <R extends T> Supplier<R> register(String id, Supplier<R> object) {
        return this.parent.register(id, object);
    }

    @Override
    public <R extends T> Supplier<R> registerConditional(String id, Supplier<R> dummy, Supplier<R> object, boolean register) {
        return this.parent.registerConditional(id, dummy, object, register);
    }

    @Nullable
    @Override
    public ResourceLocation getKey(T value) {
        return this.parent.getKey(value);
    }

    @Override
    public int getId(@Nullable T value) {
        return this.parent.getId(value);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceLocation name) {
        return this.parent.get(name);
    }

    @Nullable
    @Override
    public T byId(int id) {
        return this.parent.byId(id);
    }

    @Override
    public Optional<T> getOptional(@Nullable ResourceLocation name) {
        return this.parent.getOptional(name);
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return this.parent.key();
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return this.parent.keySet();
    }

    @Override
    public Stream<T> stream() {
        return this.parent.stream();
    }

    @Override
    public boolean containsKey(ResourceLocation name) {
        return this.parent.containsKey(name);
    }

    @Override
    public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
        return this.parent.decode(ops, input);
    }

    @Override
    public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
        return this.parent.encode(input, ops, prefix);
    }

    @Override
    public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
        return this.parent.keys(ops);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.parent.iterator();
    }

    @Override
    protected void onRegister(Platform mod) {
        this.parent.onRegister(mod);
    }
}
