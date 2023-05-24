package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pinwheel.api.animation.AnimationController;
import net.minecraft.network.FriendlyByteBuf;

/**
 * An animation controller that can be synced across the network.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface SerializableAnimationController extends AnimationController {

    /**
     * Writes data into the sync packet.
     *
     * @param buf The buffer to write into
     */
    void writeToNetwork(FriendlyByteBuf buf);

    /**
     * Reads data from the sync packet.
     *
     * @param buf The buffer to read from
     */
    void readFromNetwork(FriendlyByteBuf buf);

    /**
     * @return Whether this controller needs to be synced
     */
    boolean isDirty();

    /**
     * Sets whether this controller needs to be synced.
     *
     * @param dirty Whether this controller should be synced
     */
    void setDirty(boolean dirty);
}
