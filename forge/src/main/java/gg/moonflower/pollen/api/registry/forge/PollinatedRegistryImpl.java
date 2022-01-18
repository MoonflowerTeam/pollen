package gg.moonflower.pollen.api.registry.forge;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.platform.forge.ForgePlatform;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.util.forge.ForgeRegistryCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.*;
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
public final class PollinatedRegistryImpl<T extends IForgeRegistryEntry<T>> extends PollinatedRegistry<T> {

    private final DeferredRegister<T> registry;
    private final ForgeRegistryCodec<T> codec;
    private final Function<ResourceLocation, T> valueGetter;
    private final Function<T, ResourceLocation> keyGetter;

    private PollinatedRegistryImpl(DeferredRegister<T> deferredRegister, ForgeRegistryCodec<T> codec, String modId) {
        super(modId);
        this.registry = deferredRegister;
        this.codec = codec;
        this.valueGetter = key -> this.registry.getEntries().stream().filter(object -> object.isPresent() && object.getId().equals(key)).map(RegistryObject::get).findFirst().orElse(null);
        this.keyGetter = value -> this.registry.getEntries().stream().filter(object -> object.isPresent() && object.get().equals(value)).map(RegistryObject::getId).findFirst().orElse(null);
    }

    private PollinatedRegistryImpl(IForgeRegistry<T> registry, String modId) {
        super(modId);
        this.registry = DeferredRegister.create(registry, modId);
        this.codec = ForgeRegistryCodec.create(registry);
        this.valueGetter = registry::getValue;
        this.keyGetter = registry::getKey;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        IForgeRegistry forgeRegistry = RegistryManager.ACTIVE.getRegistry((ResourceKey) registry.key());
        return forgeRegistry != null ? new PollinatedRegistryImpl(forgeRegistry, modId) : PollinatedRegistry.createVanilla(registry, modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
        if (registry instanceof PollinatedRegistry.VanillaImpl)
            return createVanilla(((PollinatedRegistry.VanillaImpl<T>) registry).getRegistry(), modId);
        PollinatedRegistryImpl<?> impl = (PollinatedRegistryImpl<?>) registry;
        return new PollinatedRegistryImpl(impl.registry, impl.codec, modId);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> PollinatedRegistry<T> createSimple(Class<T> type, ResourceLocation registryId) {
        DeferredRegister<?> deferredRegister = createDeferredRegister((Class<? extends IForgeRegistryEntry>) type, registryId);
        return new PollinatedRegistryImpl(deferredRegister, makeCodec(deferredRegister, registryId, null), registryId.getNamespace());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> PollinatedRegistry<T> createDefaulted(Class<T> type, ResourceLocation registryId, ResourceLocation defaultId) {
        DeferredRegister<?> deferredRegister = createDeferredRegister((Class<? extends IForgeRegistryEntry>) type, registryId);
        return new PollinatedRegistryImpl(deferredRegister, makeCodec(deferredRegister, registryId, defaultId), registryId.getNamespace());
    }

    private static <T extends IForgeRegistryEntry<T>> DeferredRegister<T> createDeferredRegister(Class<T> type, ResourceLocation registryId) {
        return DeferredRegister.create(type, registryId.getNamespace());
    }

    @SuppressWarnings("unchecked")
    private static <T extends IForgeRegistryEntry<T>> ForgeRegistryCodec<T> makeCodec(DeferredRegister<?> deferredRegister, ResourceLocation registryId, @Nullable ResourceLocation defaultKey) {
        return ForgeRegistryCodec.create(((DeferredRegister<T>) deferredRegister).makeRegistry(registryId.getPath(), () -> new RegistryBuilder<T>().setDefaultKey(defaultKey)));
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

    @Nullable
    @Override
    public T get(@Nullable ResourceLocation name) {
        return this.valueGetter.apply(name);
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
        return this.codec.keys(ops);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return this.registry.getEntries().stream().filter(RegistryObject::isPresent).map(RegistryObject::get).iterator();
    }
}
