package gg.moonflower.pollen.api.config.forge;

import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class ForgeConfigValue<T> implements PollinatedConfigBuilder.ConfigValue<T> {

    private final ForgeConfigSpec.ConfigValue<T> configValue;

    ForgeConfigValue(ForgeConfigSpec.ConfigValue<T> configValue) {
        this.configValue = configValue;
    }

    @Override
    public List<String> getPath() {
        return this.configValue.getPath();
    }

    @Override
    public T get() {
        return this.configValue.get();
    }

    @Override
    public PollinatedConfigBuilder next() {
        return new PollinatedConfigBuilderImpl(this.configValue.next());
    }

    @Override
    public void save() {
        this.configValue.save();
    }

    @Override
    public void set(T value) {
        this.configValue.set(value);
    }

    @Override
    public void clearCache() {
        this.configValue.clearCache();
    }
}
