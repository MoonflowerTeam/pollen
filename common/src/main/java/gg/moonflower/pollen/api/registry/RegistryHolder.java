package gg.moonflower.pollen.api.registry;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class RegistryHolder<T> implements Supplier<T> {

    @ExpectPlatform
    static <T> RegistryHolder<T> create(PollinatedRegistry<T> registry, ResourceLocation name) {
        return Platform.error();
    }

    @ExpectPlatform
    public static <T> RegistryHolder<T> create(PollinatedRegistry<? extends T> registry, ResourceLocation name, T value) {
        return Platform.error();
    }

    public Stream<T> stream() {
        return this.isPresent() ? Stream.of(this.get()) : Stream.of();
    }

    public Optional<T> optional() {
        return this.isPresent() ? Optional.of(this.get()) : Optional.empty();
    }

    public abstract boolean isPresent();

    public abstract ResourceLocation getId();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof RegistryHolder && Objects.equals(((RegistryHolder<?>) obj).getId(), this.getId());
        }
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }
}
