package gg.moonflower.pollen.api.config.forge;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.utils.UnmodifiableConfigWrapper;
import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.Internal
public class ConfigManagerImpl {

    public static <T> T register(String modId, PollinatedConfigType type, Function<PollinatedConfigBuilder, T> consumer) {
        Pair<T, ? extends UnmodifiableConfigWrapper<UnmodifiableConfig>> pair = new PollinatedConfigBuilderImpl(new ForgeConfigSpec.Builder()).configure(consumer);
        ModContainer container = ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalStateException("Unknown mod"));
        container.addConfig(new ModConfig(byType(type), (ForgeConfigSpec) pair.getRight(), container));
        return pair.getLeft();
    }

    public static <T> T register(String modId, PollinatedConfigType type, String fileName, Function<PollinatedConfigBuilder, T> consumer) {
        Pair<T, ? extends UnmodifiableConfigWrapper<UnmodifiableConfig>> pair = new PollinatedConfigBuilderImpl(new ForgeConfigSpec.Builder()).configure(consumer);
        ModContainer container = ModList.get().getModContainerById(modId).orElseThrow(() -> new IllegalStateException("Unknown mod"));
        container.addConfig(new ModConfig(byType(type), (ForgeConfigSpec) pair.getRight(), container, fileName));
        return pair.getLeft();
    }

    private static ModConfig.Type byType(PollinatedConfigType type) {
        switch (type) {
            case COMMON:
                return ModConfig.Type.COMMON;
            case CLIENT:
                return ModConfig.Type.CLIENT;
            case SERVER:
                return ModConfig.Type.SERVER;
            default:
                throw new UnsupportedOperationException("Unknown config type: " + type);
        }
    }
}
