package gg.moonflower.pollen.core;

import gg.moonflower.pollen.api.config.PollinatedConfigBuilder;

public class PollenConfig {

    public final PollinatedConfigBuilder.ConfigValue<Double> test;

    public PollenConfig(PollinatedConfigBuilder builder) {
        builder.push("testing");
        this.test = builder.comment("test").defineInRange("peepee", 0.0, 0.0, 50.0);
        builder.pop();
    }
}
