package gg.moonflower.pollen.api.config.fabric;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.PollinatedModConfig;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public class PollinatedModConfigImpl implements PollinatedModConfig {

    private final PollinatedConfigType type;
    private final FabricConfigSpec spec;
    private final String fileName;
    private final ModContainer container;
    private final ConfigFileTypeHandler configHandler;
    private CommentedConfig configData;

    public PollinatedModConfigImpl(PollinatedConfigType type, FabricConfigSpec spec, ModContainer container, String fileName) {
        this.type = type;
        this.spec = spec;
        this.fileName = fileName;
        this.container = container;
        this.configHandler = ConfigFileTypeHandler.TOML;
    }

    @Override
    public PollinatedConfigType getType() {
        return type;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    public ConfigFileTypeHandler getHandler() {
        return configHandler;
    }

    @Override
    public FabricConfigSpec getSpec() {
        return spec;
    }

    @Override
    public String getModId() {
        return this.container.getMetadata().getId();
    }

    @Override
    public CommentedConfig getConfigData() {
        return this.configData;
    }

    void setConfigData(final CommentedConfig configData) {
        this.configData = configData;
        this.spec.setConfig(this.configData);
    }

    @Override
    public void save() {
        if (this.configData instanceof FileConfig) // Server configs without a file will be in memory instead, so no file to save to
            ((FileConfig) this.configData).save();
    }

    @Override
    public Path getFullPath() {
        return this.configData instanceof FileConfig ? ((FileConfig) this.configData).getNioPath() : null; // Same here. There is no path to a memory config.
    }
}
