package gg.moonflower.pollen.core.command.fabric;

import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.fabric.ConfigTracker;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class ConfigCommandImpl {

    @Nullable
    public static String getConfigFileName(String modId, PollinatedConfigType type) {
        return ConfigTracker.INSTANCE.getConfigFileName(modId, type);
    }
}
