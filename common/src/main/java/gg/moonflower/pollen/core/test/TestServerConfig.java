package gg.moonflower.pollen.core.test;

import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;

public class TestServerConfig {

    public final PollinatedConfigBuilder.ConfigValue<Boolean> test;

    public TestServerConfig(PollinatedConfigBuilder builder) {
        this.test = builder.define("Test", false);
    }
}
