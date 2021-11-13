package gg.moonflower.pollen.api.event;

import net.minecraft.world.InteractionResult;

/**
 * An event that can have a defined result to cancel it.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class ResultEvent extends PollinatedEvent {

    private InteractionResult result;

    public ResultEvent() {
        this.result = InteractionResult.PASS;
    }

    @Override
    public boolean isCancelled() {
        return super.isCancelled() || this.getResult() == InteractionResult.FAIL || this.getResult() == InteractionResult.SUCCESS;
    }

    /**
     * @return The result of this event
     */
    public InteractionResult getResult() {
        return result;
    }

    /**
     * Sets the result of this event.
     *
     * @param result The new result
     */
    public void setResult(InteractionResult result) {
        this.result = result;
    }
}
