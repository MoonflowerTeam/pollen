package gg.moonflower.pollen.api.blockentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Allows Fabric to have similar BlockEntity functionality as Forge.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollenBlockEntity {

    /**
     * Called when the server syncs this BlockEntity with the server.
     *
     * @param connection The connection to the server
     * @param packet     The packet received
     */
    default void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
        CompoundTag compoundtag = packet.getTag();
        if (compoundtag != null) {
            ((BlockEntity) this).load(compoundtag);
        }
    }

    /**
     * Called when the initial data from this BlockEntity is received on the client when a chunk is sent.
     *
     * @param tag The tag received
     */
    default void handleUpdateTag(CompoundTag tag) {
        ((BlockEntity) this).load(tag);
    }
}
