package gg.moonflower.pollen.api.blockentity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Allows Fabric to have the same BlockEntity functionality as Forge.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface SyncedBlockEntity {

    /**
     * Called when the server syncs this BlockEntity with the server.
     *
     * @param connection The connection to the server
     * @param packet     The packet received
     */
    default void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket packet) {
    }

    /**
     * Called when the initial data from this BlockEntity is received on the client when a chunk is sent.
     *
     * @param state The block state
     * @param tag   The tag received
     */
    default void handleUpdateTag(BlockState state, CompoundTag tag) {
        ((BlockEntity) this).load(state, tag);
    }
}
