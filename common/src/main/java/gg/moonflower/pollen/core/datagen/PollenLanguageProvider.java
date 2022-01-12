package gg.moonflower.pollen.core.datagen;

import gg.moonflower.pollen.api.datagen.provider.PollinatedLanguageProvider;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import net.minecraft.data.DataGenerator;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PollenLanguageProvider extends PollinatedLanguageProvider {

    public PollenLanguageProvider(DataGenerator generator, PollinatedModContainer container) {
        super(generator, container, "en_us");
    }

    @Override
    protected void registerTranslations() {
        this.add("pack.source.forgemod", "Forge Mod");
        this.add("argument." + this.domain + ".enum.invalid", "Invalid Enum Value");
        this.add("argument." + this.domain + ".color.invalid", "Invalid Color String");
        this.add("argument." + this.domain + ".time.unknown_unit", "Invalid Time Unit");
        this.add("argument." + this.domain + ".time.low", "Time must not be less than %s); found %s");
        this.add("argument." + this.domain + ".time.big", "Time must not be more than %s); found %s");
        this.add("commands." + this.domain + ".config.success", "Config for %s of type %s found at %s");
        this.add("commands." + this.domain + ".config.fail", "Config for %s of type %s not found");
    }
}
