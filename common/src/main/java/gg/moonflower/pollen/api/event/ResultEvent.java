package gg.moonflower.pollen.api.event;

import net.minecraft.world.InteractionResult;

/**
 * An event that can have a defined result to cancel it.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface ResultEvent extends PollinatedEvent {

    /**
     * Sets the result of this event.
     *
     * @param result The new result
     */
    void setResult(InteractionResult result);

    /**
     * @return The result of this event
     */
    InteractionResult getResult();

    @Override
    default boolean isCancelled() {
        return this.getResult() == InteractionResult.FAIL;
    }
}
