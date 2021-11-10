package gg.moonflower.pollen.api.event;

import net.minecraft.world.InteractionResult;

/**
 * An abstract event that can be fired from {@link EventDispatcher} and listened to.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedEvent {

    /**
     * An event being canceled means this event will no longer be sent to listeners that do not specify {@link EventListener#receiveCanceled()}.
     *
     * <p>On Forge this will cause Vanilla behavior to take over.
     * <p>On Fabric this will usually cause {@link InteractionResult#FAIL}. The individual event should be checked for how it can be canceled.
     *
     * @return Whether this event is canceled
     */
    default boolean isCancelled() {
        return false;
    }
}
