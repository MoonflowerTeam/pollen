package gg.moonflower.pollen.api.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * Config for a specific mod. Wrapper for Forge ModConfig
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedModConfig {

    /**
     * @return The type this config
     */
    PollinatedConfigType getType();

    /**
     * @return The name of the config file
     */
    String getFileName();

    /**
     * @return The actual config spec
     */
    UnmodifiableConfig getSpec();

    /**
     * @return The id of the mod with this config
     */
    String getModId();

    /**
     * @return The full config data
     */
    CommentedConfig getConfigData();

    /**
     * Writes the config to disc if possible.
     */
    void save();

    /**
     * @return The complete path of the config file or <code>null</code> if the config is in memory
     */
    @Nullable
    Path getFullPath();
}
