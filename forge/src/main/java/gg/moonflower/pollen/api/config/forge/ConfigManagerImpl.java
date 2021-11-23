package gg.moonflower.pollen.api.config.forge;

import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.event.events.ConfigEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.Internal
public class ConfigManagerImpl {

    public static <T> T register(String modId, PollinatedConfigType type, String fileName, Function<PollinatedConfigBuilder, T> consumer) {
        Pair<T, ForgeConfigSpec> pair = new PollinatedConfigBuilderImpl(new ForgeConfigSpec.Builder()).configure(consumer);
        ModLoadingContext.get().registerConfig(convert(type), pair.getRight(), fileName);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.<ModConfigEvent.Loading>addListener(event -> ConfigEvent.LOADING.invoker().configChanged(new PollinatedModConfigImpl(event.getConfig())));
        bus.<ModConfigEvent.Reloading>addListener(event -> ConfigEvent.RELOADING.invoker().configChanged(new PollinatedModConfigImpl(event.getConfig())));
        return pair.getLeft();
    }

    public static ModConfig.Type convert(PollinatedConfigType type) {
        return switch (type) {
            case COMMON -> ModConfig.Type.COMMON;
            case CLIENT -> ModConfig.Type.CLIENT;
            case SERVER -> ModConfig.Type.SERVER;
        };
    }

    public static PollinatedConfigType convert(ModConfig.Type type) {
        return switch (type) {
            case COMMON -> PollinatedConfigType.COMMON;
            case CLIENT -> PollinatedConfigType.CLIENT;
            case SERVER -> PollinatedConfigType.SERVER;
        };
    }
}
