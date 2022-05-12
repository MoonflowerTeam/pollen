package gg.moonflower.pollen.api.registry.forge;

import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.RegistryHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolderRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

@ApiStatus.Internal
public class RegistryHolderImpl<T> extends RegistryHolder<T> {

    private final ResourceLocation name;
    private T value;

    private RegistryHolderImpl(PollinatedRegistry<? extends T> registry, ResourceLocation name) {
        this.name = name;
        this.value = null;
        ObjectHolderRegistry.addHandler(pred ->
        {
            if (pred.test(registry.key().getRegistryName()))
                this.value = registry.containsKey(name) ? registry.get(name) : null;
        });
    }

    @Override
    public boolean isPresent() {
        return this.value != null;
    }

    @Override
    public ResourceLocation getId() {
        return name;
    }

    @Override
    public T get() {
        T ret = this.value;
        Objects.requireNonNull(ret, () -> "Registry Object not present: " + this.name);
        return ret;
    }

    public static <T> RegistryHolder<T> create(PollinatedRegistry<T> registry, ResourceLocation name) {
        return new RegistryHolderImpl<>(registry, name);
    }

    public static <T> RegistryHolder<T> create(PollinatedRegistry<? extends T> registry, ResourceLocation name, T value) {
        RegistryHolderImpl<T> impl = new RegistryHolderImpl<>(registry, name);
        impl.value = value;
        return impl;
    }
}
