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

    private PlayerInteractionEvents() {}

    @FunctionalInterface
    public interface RightClickItem {
        InteractionResultHolder<ItemStack> interaction(Player player, Level level, InteractionHand hand);
    }

    @FunctionalInterface
    public interface RightClickEntity {
        InteractionResult interaction(Player player, Level level, InteractionHand hand, Entity entity);
    }

    @FunctionalInterface
    public interface RightClickBlock {
        InteractionResult interaction(Player player, Level level, InteractionHand hand, BlockHitResult hitResult);
    }

    @FunctionalInterface
    public interface LeftClickBlock {
        InteractionResult interaction(Player player, Level level, InteractionHand hand, BlockPos pos, Direction direction);
    }


}
