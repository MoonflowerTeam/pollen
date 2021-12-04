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
    default void onAddedToLevel() {
    }

    /**
     * Called after this entity has been removed from the ticking list.
     */
    default void onRemovedFromLevel() {
    }

    /**
     * Used in model rendering to determine if the entity riding this entity should be in the 'sitting' position.
     *
     * @return false to prevent an entity that is mounted to this entity from displaying the 'sitting' animation.
     */
    default boolean shouldRiderSit() {
        return true;
    }
}
