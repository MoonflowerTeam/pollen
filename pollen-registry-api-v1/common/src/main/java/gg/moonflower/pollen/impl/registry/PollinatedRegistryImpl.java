package gg.moonflower.pollen.impl.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import gg.moonflower.pollen.api.registry.v1.PollinatedBlockRegistry;
import gg.moonflower.pollen.api.registry.v1.PollinatedEntityRegistry;
import gg.moonflower.pollen.api.registry.v1.PollinatedFluidRegistry;
import gg.moonflower.pollen.api.registry.v1.PollinatedRegistry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@ApiStatus.Internal
public abstract class PollinatedRegistryImpl<T> implements PollinatedRegistry<T> {

    protected final String modId;
    private boolean registered;

    protected PollinatedRegistryImpl(String modId) {
        this.modId = modId;
    }

    @ExpectPlatform
    public static <T> PollinatedRegistryImpl<T> create(Registry<T> registry, String modId) {
        return Platform.error();
    }

    @ExpectPlatform
    public static <T> PollinatedRegistryImpl<T> create(PollinatedRegistry<T> registry, String modId) {
        return Platform.error();
    }

    @Override
    public String getModId() {
        return modId;
    }

    /**
     * Initializes the registry for a {@link Platform}.
     *
     * @param mod The {@link Platform} to register the registry onto.
     * @throws IllegalStateException if the registry has already been registered.
     */
    @Override
    public final void register(Platform mod) {
        if (this.registered)
            throw new IllegalStateException("Cannot register a PollinatedRegistry twice!");
        this.registered = true;
        this.onRegister(mod);
    }

    @ApiStatus.Internal
    public static class VanillaImpl<T> extends PollinatedRegistryImpl<T> {

        private final Registry<T> registry;
        private final Codec<T> codec;

        public VanillaImpl(Registry<T> registry, String modId) {
            super(modId);
            this.registry = registry;
            this.codec = this.registry.byNameCodec();
        }

        public Registry<T> getRegistry() {
            return registry;
        }

        @Override
        public <R extends T> Supplier<R> register(String id, Supplier<R> object) {
            R registered = Registry.register(this.registry, new ResourceLocation(this.modId, id), object.get());
            return () -> registered;
        }

        @Nullable
        @Override
        public ResourceLocation getKey(T value) {
            return this.registry.getKey(value);
        }

        @Override
        public int getId(@Nullable T value) {
            return this.registry.getId(value);
        }

        @Nullable
        @Override
        public T get(@Nullable ResourceLocation name) {
            return this.registry.get(name);
        }

        @Nullable
        @Override
        public T byId(int id) {
            return this.registry.byId(id);
        }

        @Override
        public ResourceKey<? extends Registry<T>> key() {
            return this.registry.key();
        }

        @Override
        public Set<ResourceLocation> keySet() {
            return this.registry.keySet();
        }

        @Override
        public boolean containsKey(ResourceLocation name) {
            return this.registry.containsKey(name);
        }

        @Override
        public <T1> DataResult<Pair<T, T1>> decode(DynamicOps<T1> ops, T1 input) {
            return this.codec.decode(ops, input);
        }

        @Override
        public <T1> DataResult<T1> encode(T input, DynamicOps<T1> ops, T1 prefix) {
            return this.codec.encode(input, ops, prefix);
        }

        @Override
        public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
            return this.registry.keys(ops);
        }

        @NotNull
        @Override
        public Iterator<T> iterator() {
            return this.registry.iterator();
        }
    }
}
