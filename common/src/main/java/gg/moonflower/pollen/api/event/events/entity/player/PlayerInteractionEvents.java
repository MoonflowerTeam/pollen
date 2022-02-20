package gg.moonflower.pollen.api.event.events.entity.player;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public final class PlayerInteractionEvents {

    public static final PollinatedEvent<RightClickItem> RIGHT_CLICK_ITEM = EventRegistry.create(RightClickItem.class, events -> (player, level, hand) -> {
        for (RightClickItem event : events) {
            InteractionResultHolder<ItemStack> result = event.interaction(player, level, hand);
            if (result.getResult() != InteractionResult.PASS)
                return result;
        }
        return InteractionResultHolder.pass(ItemStack.EMPTY);
    });

    public static final PollinatedEvent<RightClickEntity> RIGHT_CLICK_ENTITY = EventRegistry.createResult(RightClickEntity.class);
    public static final PollinatedEvent<RightClickBlock> RIGHT_CLICK_BLOCK = EventRegistry.createResult(RightClickBlock.class);
    public static final PollinatedEvent<LeftClickBlock> LEFT_CLICK_BLOCK = EventRegistry.createResult(LeftClickBlock.class);

    private PlayerInteractionEvents() {
    }

    /**
     * Fired each time a player right-clicks an item.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface RightClickItem {

        /**
         * Called when the specified player right-clicks their held item.
         *
         * @param player The player right-clicking
         * @param level  The level the player is in
         * @param hand   The hand the item is in
         * @return A result for this interaction. {@link InteractionResultHolder#pass(Object)} will continue onto the next iteration, while any others will override vanilla behavior
         */
        InteractionResultHolder<ItemStack> interaction(Player player, Level level, InteractionHand hand);
    }

    /**
     * Fired each time a player right-clicks an entity.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface RightClickEntity {

        /**
         * Called when the specified player interacts with the specified entity.
         *
         * @param player The player right-clicking
         * @param level  The level the player is in
         * @param hand   The hand the player is clicking with
         * @param entity The entity being clicked
         * @return The result for this interaction. {@link InteractionResult#PASS} will continue onto the next iteration, while any others will override vanilla behavior
         */
        InteractionResult interaction(Player player, Level level, InteractionHand hand, Entity entity);
    }

    /**
     * Fired each time a player right-clicks an item onto a block.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface RightClickBlock {

        /**
         * Called when the specified player interacts with the specified block.
         *
         * @param player    The player right-clicking
         * @param level     The level the player is in
         * @param hand      The hand the player is clicking with
         * @param hitResult The ray trace result onto a block
         * @return The result for this interaction. {@link InteractionResult#PASS} will continue onto the next iteration, while any others will override vanilla behavior
         */
        InteractionResult interaction(Player player, Level level, InteractionHand hand, BlockHitResult hitResult);
    }

    /**
     * Fired each time a player left-clicks a block.
     *
     * @author Jackson
     * @since 1.0.0
     */
    @FunctionalInterface
    public interface LeftClickBlock {

        /**
         * Called when the specified player left-clicks the specified block
         *
         * @param player    The player right-clicking
         * @param level     The level the player is in
         * @param hand      The hand the player is clicking with
         * @param pos       The position being punched
         * @param direction The side of the block being punched
         * @return The result for this interaction. {@link InteractionResult#PASS} will continue onto the next iteration, while any others will override vanilla behavior
         */
        InteractionResult interaction(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction);
    }
}
