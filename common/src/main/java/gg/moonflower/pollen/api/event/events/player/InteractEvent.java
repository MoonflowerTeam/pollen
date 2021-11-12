package gg.moonflower.pollen.api.event.events.player;

import gg.moonflower.pollen.api.event.ResultEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Events fired for player interactions.
 */
public class InteractEvent extends ResultEvent {
    private final Player player;
    private final Level level;
    private final InteractionHand hand;

    public InteractEvent(Player player, Level level, InteractionHand hand) {
        this.player = player;
        this.level = level;
        this.hand = hand;
    }

    public Player getPlayer() {
        return player;
    }

    public Level getLevel() {
        return level;
    }

    public InteractionHand getHand() {
        return hand;
    }

    /**
     * Called each time a player right-clicks an item.
     *
     * @author Jackson
     * @since 1.0.0
     */
    public static class UseItem extends InteractEvent {
        public UseItem(Player player, Level level, InteractionHand hand) {
            super(player, level, hand);
        }
    }

    /**
     * Called each time a player right-clicks an entity.
     *
     * @author Jackson
     * @since 1.0.0
     */
    public static class UseEntity extends InteractEvent {
        private final Entity entity;

        public UseEntity(Player player, Level level, InteractionHand hand, Entity entity) {
            super(player, level, hand);
            this.entity = entity;
        }

        public Entity getEntity() {
            return entity;
        }
    }

    /**
     * Called each time a player right-clicks a block.
     *
     * @author Jackson
     * @since 1.0.0
     */
    public static class UseBlock extends InteractEvent {
        private final BlockHitResult hitResult;

        public UseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult) {
            super(player, level, hand);
            this.hitResult = hitResult;
        }

        public BlockHitResult getHitResult() {
            return hitResult;
        }
    }

    /**
     * Called each time a player left-clicks a block.
     *
     * @author Jackson
     * @since 1.0.0
     */
    public static class AttackBlock extends InteractEvent {
        private final BlockPos pos;
        private final Direction direction;

        public AttackBlock(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction) {
            super(player, level, hand);
            this.pos = pos;
            this.direction = direction;
        }

        public BlockPos getPos() {
            return pos;
        }

        public Direction getDirection() {
            return direction;
        }
    }
}
