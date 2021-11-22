package gg.moonflower.pollen.api.config.forge;

import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.event.events.ConfigEvent;
import gg.moonflower.pollen.api.platform.Platform;
import gg.moonflower.pollen.core.forge.PollenForge;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
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
        bus.<ModConfig.Loading>addListener(event -> PollenForge.postEvent(event, new ConfigEvent.Loading(new PollinatedModConfigImpl(event.getConfig()))));
        bus.<ModConfig.Reloading>addListener(event -> PollenForge.postEvent(event, new ConfigEvent.Reloading(new PollinatedModConfigImpl(event.getConfig()))));
        return pair.getLeft();
    }

    public static ModConfig.Type convert(PollinatedConfigType type) {
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

    public static PollinatedConfigType convert(ModConfig.Type type) {
        switch (type) {
            case COMMON:
                return PollinatedConfigType.COMMON;
            case CLIENT:
                return PollinatedConfigType.CLIENT;
            case SERVER:
                return PollinatedConfigType.SERVER;
            default:
                throw new UnsupportedOperationException("Unknown config type: " + type);
        }
    }
}
