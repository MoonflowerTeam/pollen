package gg.moonflower.pollen.api.util.forge;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.stream.Stream;

/**
 * Creates a {@link Keyable} {@link Codec} from a Forge Registry to restore lost functionality in Vanilla.
 *
 * @param <T> The type of object returned by the registry
 * @author Ocelot
 * @see Registry
 * @since 1.0.0
 */
public class ForgeRegistryCodec<T extends IForgeRegistryEntry<T>> implements Codec<T>, Keyable {

    private final IForgeRegistry<T> registry;

    /**
     * Creates a new forge registry codec for the specified forge registry.
     *
     * @param registry The registry to wrap
     * @param <T>      The type of object the registry registers
     * @return A new codec for that registry
     */
    public static <T extends IForgeRegistryEntry<T>> ForgeRegistryCodec<T> create(IForgeRegistry<T> registry) {
        return new ForgeRegistryCodec<T>(registry);
    }

    private ForgeRegistryCodec(IForgeRegistry<T> registry) {
        this.registry = registry;
    }

    @Override
    public <U> DataResult<Pair<T, U>> decode(DynamicOps<U> ops, U input) {
        return ResourceLocation.CODEC.decode(ops, input).flatMap(pair ->
        {
            T t = this.registry.getValue(pair.getFirst());
            return t == null ? DataResult.error("Unknown registry key: " + pair.getFirst()) : DataResult.success(Pair.of(t, pair.getSecond()));
        });
    }

    @Override
    public <U> DataResult<U> encode(T input, DynamicOps<U> ops, U prefix) {
        ResourceLocation resourcelocation = this.registry.getKey(input);
        if (resourcelocation == null)
            return DataResult.error("Unknown registry element " + input);
        return ops.mergeToPrimitive(prefix, ops.createString(resourcelocation.toString()));
    }

    @Override
    public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
        return this.registry.getKeys().stream().map(location -> ops.createString(location.toString()));
    }
}
