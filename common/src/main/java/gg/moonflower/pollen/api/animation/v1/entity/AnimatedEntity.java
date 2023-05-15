package gg.moonflower.pollen.api.animation.v1.entity;

import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pollen.api.animation.v1.controller.SerializableAnimationController;
import gg.moonflower.pollen.core.network.PollenMessages;
import gg.moonflower.pollen.core.network.play.ClientboundSetAnimationPacket;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

/**
 * Marks an entity as having custom animations driven by an animation controller.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface AnimatedEntity {

    /**
     * @return The controller of animations for this entity
     */
    AnimationController getAnimationController();

    /**
     * Syncs the animations from this controller with the client on the server if possible.
     *
     * @throws IllegalStateException If the value of {@link #getAnimationController()} is not an instance of {@link SerializableAnimationController}
     */
    default void syncClient() {
        if (!(this.getAnimationController() instanceof SerializableAnimationController serializableController)) {
            throw new IllegalStateException("Animation controller must be serializable to sync");
        }
        if (!(this instanceof Entity entity)) {
            throw new IllegalStateException(this.getClass().getName() + " must extend Entity");
        }
        if (!serializableController.isDirty()) {
            return;
        }
        serializableController.setDirty(false);

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        serializableController.writeToNetwork(buf);
        PollenMessages.PLAY.sendToTracking(entity, new ClientboundSetAnimationPacket(entity.getId(), buf));
    }
}
