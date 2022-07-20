package gg.moonflower.pollen.core.datagen;

import gg.moonflower.pollen.api.datagen.provider.PollinatedLanguageProvider;
import gg.moonflower.pollen.api.entity.PollenEntityTypes;
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
        this.add("options." + this.domain + ".entitlementList", "Moonflower Cosmetics...");
        this.add("options." + this.domain + ".entitlementList.title", "Moonflower Cosmetic Settings");
        this.add("options." + this.domain + ".entitlementList.none", "You have no cosmetics");
        this.add("options." + this.domain + ".entitlementList.reload", "Refresh");
        this.add("argument." + this.domain + ".enum.invalid", "Invalid Enum Value");
        this.add("argument." + this.domain + ".color.invalid", "Invalid Color String");
        this.add("argument." + this.domain + ".time.unknown_unit", "Invalid Time Unit");
        this.add("argument." + this.domain + ".time.low", "Time must not be less than %s); found %s");
        this.add("argument." + this.domain + ".time.big", "Time must not be more than %s); found %s");
        this.add("commands." + this.domain + ".config.success", "Config for %s of type %s found at %s");
        this.add("commands." + this.domain + ".config.fail", "Config for %s of type %s not found");
        this.add("screen." + this.domain + ".moonflowerProfilesDisabled", "Moonflower profiles are disabled. To see or manage cosmetics set '%s' in the client config to false.");
        this.add("screen." + this.domain + ".moonflowerServerDown.header", "Moonflower Servers are Down");
        this.add("screen." + this.domain + ".moonflowerServerDown.message", "Unfortunately, the cosmetic servers are down right now so you will not be able to see or manage cosmetics. Please try again later.");
        this.add("screen." + this.domain + ".linkPatreon.header", "Link Patreon");
        this.add("screen." + this.domain + ".linkPatreon.message", "You must link your Minecraft account to your Patreon account before you can access your Moonflower cosmetics. This will continue in an external browser, do you wish to proceed?");
        this.add("screen." + this.domain + ".linkPatreon.waiting", "Waiting for response from server...");
        this.add("screen." + this.domain + ".linkPatreon.error", "Failed to link Patreon");
        this.add("gui.jei.category." + this.domain + ".grindstone.experience", "XP: %s - %s");
        this.addEntityType(PollenEntityTypes.BOAT, "Boat");
    }
}
