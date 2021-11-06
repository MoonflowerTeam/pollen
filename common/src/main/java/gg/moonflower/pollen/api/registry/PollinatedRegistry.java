package gg.moonflower.pollen.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * An abstracted registry for wrapping platform-specific registries.
 *
 * @param <T> The object type.
 * @author Jackson
 * @since 1.0.0
 */
public abstract class PollinatedRegistry<T> {
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
     * @param <T>      The object type.
     * @return A {@link PollinatedRegistry} backed by a platform-specific registry.
     */
    @ExpectPlatform
    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        return Platform.error();
    }

    /**
     * Creates an {@link PollinatedRegistry} backed by a {@link Registry}.
     * <p>Users should always use {@link PollinatedRegistry#create(Registry, String)}.
     * <p>This is for very specific cases where vanilla registries must strictly be used and {@link PollinatedRegistry#create(Registry, String)} can't do what you need.
     *
     * @param registry The registry to register objects to.
     * @param modId    The mod id to register to.
     * @param <T>      The object type.
     * @return A {@link PollinatedRegistry} backed by a {@link Registry}.
     */
    public static <T> PollinatedRegistry<T> createVanilla(Registry<T> registry, String modId) {
        return new VanillaImpl<>(registry, modId);
    }

    /**
     * Registers an object.
     *
     * @param id     The id of the object.
     * @param object The object to register.
     * @param <I>    The type of object.
     * @return The registered object in a supplier.
     */
    public abstract <I extends T> Supplier<I> register(String id, Supplier<I> object);

    /**
     * Initializes the registry for a {@link Platform}.
     *
     * @param mod The {@link Platform} to register the registry onto.
     * @throws IllegalStateException if the registry has already been registered.
     */
    @ApiStatus.NonExtendable
    public void register(Platform mod) {
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

        private VanillaImpl(Registry<T> registry, String modId) {
            super(modId);
            this.registry = registry;
        }

        @Override
        public <I extends T> Supplier<I> register(String id, Supplier<I> object) {
            I registered = Registry.register(this.registry, new ResourceLocation(this.modId, id), object.get());
            return () -> registered;
        }
    }
}
