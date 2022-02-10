package gg.moonflower.pollen.api.block;

import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;

public class PollinatedLiquidBlock extends LiquidBlock {

    public PollinatedLiquidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (this.shouldSpreadLiquid(level, pos))
            level.getLiquidTicks().scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (this.shouldSpreadLiquid(level, pos))
            level.getLiquidTicks().scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
    }

    private boolean shouldSpreadLiquid(Level level, BlockPos pos) {
        if (!(this.fluid instanceof PollinatedFluid))
            return true;

        PollinatedFluid behavior = (PollinatedFluid) this.fluid;
        FluidState fluidState = level.getFluidState(pos);
        Direction[] directions = behavior.getInteractionDirections();
        for (Direction direction : directions) {
            BlockPos blockPos = pos.relative(direction);
            BlockState interactionState = behavior.getInteractionState(level, fluidState, pos, blockPos);
            if (interactionState != null) {
                level.setBlockAndUpdate(pos, interactionState);
                behavior.playInteractionEffect(level, fluidState, pos);
                return false;
            }
        }

        return true;
    }
}
