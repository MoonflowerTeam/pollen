package gg.moonflower.pollen.api.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.Registry;
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

/**
 * An abstracted registry for wrapping platform-specific registries.
 *
 * @param <T> The object type
 * @author Jackson
 * @since 1.0.0
 */
public abstract class PollinatedRegistry<T> implements Codec<T>, Keyable, Iterable<T> {

    protected final String modId;
    private boolean registered;

    protected PollinatedRegistry(String modId) {
        this.modId = modId;
    }

    /**
     * Creates an {@link PollinatedRegistry} backed by a platform-specific registry.
     * <p>Forge users: If there's no ForgeRegistry for the object type, this will return a {@link PollinatedRegistry.VanillaImpl}.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The registry type.
     * @return A {@link PollinatedRegistry} backed by a platform-specific registry.
     */
    @ExpectPlatform
    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return Platform.error();
    }

    /**
     * Creates a {@link PollinatedRegistry} backed by a platform-specific registry. This should only be used to register to another mod's registry.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The registry type.
     * @return A {@link PollinatedRegistry} backed by a platform-specific registry.
     */
    @ExpectPlatform
    public static <T> PollinatedRegistry<T> create(PollinatedRegistry<T> registry, String modId) {
        return Platform.error();
    }

    /**
     * Creates a {@link PollinatedRegistry} for registering blocks and item blocks. The mod id from the item registry is used as the id for the block registry.
     *
     * @param itemRegistry The registry to add items to
     * @return A specialized block registry that can register items
     */
    public static PollinatedBlockRegistry createBlock(PollinatedRegistry<Item> itemRegistry) {
        return new PollinatedBlockRegistry(create(Registry.BLOCK, itemRegistry.getModId()), itemRegistry);
    }

    /**
     * Creates a {@link PollinatedRegistry} backed by a {@link Registry}.
     * <p>Users should always use {@link PollinatedRegistry#create(Registry, String)}.
     * <p>This is for very specific cases where vanilla registries must strictly be used and {@link PollinatedRegistry#create(Registry, String)} can't do what you need.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The registry type.
     * @return A {@link PollinatedRegistry} backed by a {@link Registry}.
     */
    public static <T> PollinatedRegistry<T> createVanilla(Registry<T> registry, String modId) {
        return new VanillaImpl<>(registry, modId);
    }

    /**
     * Creates a new simple registry
     *
     * @param registryId The registry {@link ResourceLocation} used as the registry id
     * @param <T>        The type stored in the Registry
     * @return An instance of FabricRegistryBuilder
     */
    @ExpectPlatform
    public static <T> PollinatedRegistry<T> createSimple(Class<T> type, ResourceLocation registryId) {
        return Platform.error();
    }

    /**
     * @param registryId The registry {@link ResourceLocation} used as the registry id
     * @param defaultId  The default registry id
     * @param <T>        The type stored in the Registry
     * @return An instance of FabricRegistryBuilder
     */
    @ExpectPlatform
    public static <T> PollinatedRegistry<T> createDefaulted(Class<T> type, ResourceLocation registryId, ResourceLocation defaultId) {
        return Platform.error();
    }

    /**
     * @return The id of the mod this registry is for
     */
    public String getModId() {
        return modId;
    }

    /**
     * Registers an object.
     *
     * @param id     The id of the object.
     * @param object The object to register.
     * @param <R>    The registry type.
     * @return The registered object in a {@link Supplier}.
     */
    public abstract <R extends T> Supplier<R> register(String id, Supplier<R> object);

    /**
     * Registers an object or a dummy object based on a condition.
     *
     * @param id       The id of the object.
     * @param dummy    The object to register if the condition is false.
     * @param object   The object to register if the condition is true.
     * @param register Whether the object should be registered or the dummy should be registered.
     * @param <R>      The registry type.
     * @return The registered object in a {@link Supplier}
     */
    public <R extends T> Supplier<R> registerConditional(String id, Supplier<R> dummy, Supplier<R> object, boolean register) {
        return this.register(id, register ? object : dummy);
    }

    /**
     * Retrieves the key for the specified value.
     *
     * @param value The value to get the key for
     * @return A key for that value or <code>null</code> if this registry doesn't contain that value
     */
    @Nullable
    public abstract ResourceLocation getKey(T value);

    /**
     * Retrieves the value for the specified key.
     *
     * @param name The key to get the value for
     * @return A value for that key or <code>null</code> if this registry doesn't contain a value with that name
     */
    @Nullable
    public abstract T get(@Nullable ResourceLocation name);

    /**
     * Retrieves the value for the specified key.
     *
     * @param name The key to get the value for
     * @return A value for that key
     */
    public Optional<T> getOptional(@Nullable ResourceLocation name) {
        return Optional.ofNullable(this.get(name));
    }

    /**
     * @return A set of all registered keys in the registry
     */
    public abstract Set<ResourceLocation> keySet();

    /**
     * @return A stream of all values in the registry
     */
    public Stream<T> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    /**
     * Checks to see if a value with the specified name exists.
     *
     * @param name The name of the key to get
     * @return Whether that value exists
     */
    public abstract boolean containsKey(ResourceLocation name);

    /**
     * Initializes the registry for a {@link Platform}.
     *
     * @param mod The {@link Platform} to register the registry onto.
     * @throws IllegalStateException if the registry has already been registered.
     */
    public final void register(Platform mod) {
        if (this.registered)
            throw new IllegalStateException("Cannot register a PollinatedRegistry twice!");
        this.registered = true;
        this.onRegister(mod);
    }

    @ApiStatus.OverrideOnly
    protected void onRegister(Platform mod) {
    }

    @ApiStatus.Internal
    public static class VanillaImpl<T> extends PollinatedRegistry<T> {

        private final Registry<T> registry;
        private final Codec<T> codec;

        private VanillaImpl(Registry<T> registry, String modId) {
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

        @Nullable
        @Override
        public T get(@Nullable ResourceLocation name) {
            return this.registry.get(name);
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
