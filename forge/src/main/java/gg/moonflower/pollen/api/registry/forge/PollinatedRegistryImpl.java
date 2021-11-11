package gg.moonflower.pollen.api.registry.forge;

import com.mojang.serialization.Codec;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.api.platform.forge.ForgePlatform;
import gg.moonflower.pollen.api.registry.PollinatedRegistry;
import gg.moonflower.pollen.api.util.ForgeRegistryCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

@ApiStatus.Internal
public final class PollinatedRegistryImpl<T extends IForgeRegistryEntry<T>> extends PollinatedRegistry<T> {

    private final DeferredRegister<T> registry;
    private final Codec<T> codec;

    private PollinatedRegistryImpl(IForgeRegistry<T> registry, String modId) {
        super(modId);
        this.registry = DeferredRegister.create(registry, modId);
        this.codec = ForgeRegistryCodec.create(registry);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> PollinatedRegistry<T> create(Registry<T> registry, String modId) {
        IForgeRegistry forgeRegistry = RegistryManager.ACTIVE.getRegistry((ResourceKey) registry.key());
        return forgeRegistry != null ? new PollinatedRegistryImpl(forgeRegistry, modId) : PollinatedRegistry.createVanilla(registry, modId);
    }

    @Override
    public Supplier<T> register(String id, Supplier<T> object) {
        return this.registry.register(id, object);
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }

    @Override
    protected void onRegister(Platform mod) {
        this.registry.register(((ForgePlatform) mod).getEventBus());
    }
}
