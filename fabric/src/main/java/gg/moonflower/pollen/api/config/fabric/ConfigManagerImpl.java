package gg.moonflower.pollen.api.config.fabric;

import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Function;

@ApiStatus.Internal
public class ConfigManagerImpl {

    public static <T> T register(String modId, PollinatedConfigType type, String fileName, Function<PollinatedConfigBuilder, T> consumer) {
        Pair<T, FabricConfigSpec> pair = new PollinatedConfigBuilderImpl().configure(consumer);
        ConfigTracker.INSTANCE.trackConfig(new PollinatedModConfigImpl(type, pair.getRight(), FabricLoader.getInstance().getModContainer(modId).orElseThrow(() -> new IllegalStateException("Unknown mod: " + modId)), fileName));
        return pair.getLeft();
    }

    public static Optional<PollinatedModConfigImpl> get(String modId, PollinatedConfigType type) {
        return ConfigTracker.INSTANCE.getConfig(modId, type);
    }
}
