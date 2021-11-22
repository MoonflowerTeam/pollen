package gg.moonflower.pollen.api.event.events;

import gg.moonflower.pollen.api.config.PollinatedModConfig;
import gg.moonflower.pollen.api.event.PollinatedEvent;

/**
 * Events fired for config data changes.
 */
public class ConfigEvent extends PollinatedEvent {

    private final PollinatedModConfig config;

    private ConfigEvent(PollinatedModConfig config) {
        this.config = config;
    }

    public String getModId() {
        return this.config.getModId();
    }

    public PollinatedModConfig getConfig() {
        return config;
    }

    /**
     * Called each time the config is loaded from disc.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Loading extends ConfigEvent {
        public Loading(PollinatedModConfig config) {
            super(config);
        }
    }

    /**
     * Called each time the config is reloaded from disc. Usually from updating the config file in some way.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    public static class Reloading extends ConfigEvent {
        public Reloading(PollinatedModConfig config) {
            super(config);
        }
    }
}
