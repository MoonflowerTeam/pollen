package gg.moonflower.pollen.api.registry.forge;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.platform.forge.ForgePlatform;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApiStatus.Internal
public final class PollinatedRegistryImpl<T> extends PollinatedRegistry<T> {

    private final DeferredRegister<T> registry;
    private final Codec<T> codec;
    private final Keyable keyable;
    private final ResourceKey<? extends Registry<T>> resourceKey;
    private final Function<ResourceLocation, T> valueGetter;
    private final Function<Integer, T> valueIdGetter;
    private final Function<T, ResourceLocation> keyGetter;
    private final Function<T, Integer> keyIdGetter;

    private PollinatedRegistryImpl(DeferredRegister<T> deferredRegister, Codec<T> codec, Keyable keyable, ResourceKey<? extends Registry<T>> resourceKey, String modId) {
        super(modId);
        this.registry = deferredRegister;
        this.codec = codec;
        this.keyable = keyable;
        this.resourceKey = resourceKey;
        this.valueGetter = key -> this.registry.getEntries().stream().filter(object -> object.isPresent() && object.getId().equals(key)).map(RegistryObject::get).findFirst().orElse(null);
        this.valueIdGetter = key -> {
            throw new IllegalStateException("Use Vanilla registries to fetch IDs");
        };
        this.keyGetter = value -> this.registry.getEntries().stream().filter(object -> object.isPresent() && object.get().equals(value)).map(RegistryObject::getId).findFirst().orElse(null);
        this.keyIdGetter = value -> {
            throw new IllegalStateException("Use Vanilla registries to fetch IDs");
        };
    }

    private PollinatedRegistryImpl(Registry<T> registry, String modId) {
        super(modId);
        this.registry = DeferredRegister.create(registry.key(), modId);
        this.codec = registry.byNameCodec();
        this.keyable = registry;
        this.resourceKey = registry.key();
        this.valueGetter = registry::get;
        this.valueIdGetter = registry::byId;
        this.keyGetter = registry::getKey;
        this.keyIdGetter = registry::getId;
    }

    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return new PollinatedRegistryImpl<>(registry, modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
        if (registry instanceof PollinatedRegistry.VanillaImpl)
            return createVanilla(((PollinatedRegistry.VanillaImpl<T>) registry).getRegistry(), modId);
        PollinatedRegistryImpl<?> impl = (PollinatedRegistryImpl<?>) registry;
        return new PollinatedRegistryImpl(impl.registry, impl.codec, impl.keyable, impl.resourceKey, modId);
    }

    @Override
    public <R extends T> Supplier<R> register(String id, Supplier<R> object) {
        return this.registry.register(id, object);
    }

    @Nullable
    @Override
    public ResourceLocation getKey(T value) {
        return this.keyGetter.apply(value);
    }

    @Override
    public int getId(@Nullable T value) {
        return this.keyIdGetter.apply(value);
    }

    @Nullable
    @Override
    public T get(@Nullable ResourceLocation name) {
        return this.valueGetter.apply(name);
    }

    @Nullable
    @Override
    public T byId(int id) {
        return this.valueIdGetter.apply(id);
    }

    @Override
    public ResourceKey<? extends Registry<T>> key() {
        return resourceKey;
    }

    @Override
    public Set<ResourceLocation> keySet() {
        return this.registry.getEntries().stream().map(RegistryObject::getId).collect(Collectors.toSet());
    }

    @Override
    public boolean containsKey(ResourceLocation name) {
        return this.registry.getEntries().stream().anyMatch(object -> object.getId().equals(name));
    }

    @Override
    protected void onRegister(Platform mod) {
        this.registry.register(((ForgePlatform) mod).getEventBus());
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
        return this.keyable.keys(ops);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.registry.getEntries().stream().filter(RegistryObject::isPresent).map(RegistryObject::get).iterator();
    }
}
