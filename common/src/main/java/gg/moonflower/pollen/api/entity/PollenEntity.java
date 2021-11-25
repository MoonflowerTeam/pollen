package gg.moonflower.pollen.api.entity;

/**
 * Allows Fabric to have similar Entity functionality as Forge.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollenEntity {

    /**
     * Called after this entity has been added to the ticking list.
     */
    void onAddedToWorld();

    /**
     * Called after this entity has been removed from the ticking list.
     */
    void onRemovedFromWorld();
}
