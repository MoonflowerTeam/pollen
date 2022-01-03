package gg.moonflower.pollen.api.datagen.provider.model;

/**
 * Generates block states and models for blocks and items.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedModelGenerator {

    /**
     * Creates all the models just before saving all files.
     */
    void run();
}
