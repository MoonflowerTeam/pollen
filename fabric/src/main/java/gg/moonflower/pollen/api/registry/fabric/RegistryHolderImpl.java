package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.registry.RegistryHolder;
import net.minecraft.resources.ResourceLocation;

public class RegistryHolderImpl {
    public static RegistryHolder<T> create(ResourceLocation name) {
    }

    public static RegistryHolder<T> create(PollinatedRegistry<V> registry, ResourceLocation name, T value) {
    }
}
