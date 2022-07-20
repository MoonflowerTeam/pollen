package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;

public class PollenClientConfig {

    public final PollinatedConfigBuilder.ConfigValue<Boolean> disableMoonflowerProfiles;

    public PollenClientConfig(PollinatedConfigBuilder builder) {
        this.disableMoonflowerProfiles = builder.comment("Disables any connection to Moonflower servers, including all Moonflower cosmetics. The game must be restarted for the changes to apply.").define("Disable Moonflower Profiles", false);
    }
}
