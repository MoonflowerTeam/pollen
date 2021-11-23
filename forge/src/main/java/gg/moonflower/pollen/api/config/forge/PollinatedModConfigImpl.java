package gg.moonflower.pollen.api.config.forge;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import gg.moonflower.pollen.api.config.PollinatedConfigType;
import gg.moonflower.pollen.api.config.PollinatedModConfig;
import net.minecraftforge.fml.config.ModConfig;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public class PollinatedModConfigImpl implements PollinatedModConfig {

    private final ModConfig config;

    public PollinatedModConfigImpl(ModConfig config) {
        this.config = config;
    }

    @Override
    public PollinatedConfigType getType() {
        return ConfigManagerImpl.convert(this.config.getType());
    }

    @Override
    public String getFileName() {
        return this.config.getFileName();
    }

    @Override
    public UnmodifiableConfig getSpec() {
        return this.config.getSpec();
    }

    @Override
    public String getModId() {
        return this.config.getModId();
    }

    @Override
    public CommentedConfig getConfigData() {
        return this.config.getConfigData();
    }

    @Override
    public void save() {
        this.config.save();
    }

    @Override
    public Path getFullPath() {
        return this.config.getFullPath();
    }
}
