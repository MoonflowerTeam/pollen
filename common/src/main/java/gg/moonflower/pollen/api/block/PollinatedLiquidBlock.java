package gg.moonflower.pollen.api.block;

import gg.moonflower.pollen.api.fluid.PollenFluidBehavior;
import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
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

//    @Override
//    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
//        return context.isAbove(STABLE_SHAPE, pos, true) && state.getValue(LEVEL) == 0 && context.canStandOnFluid(level.getFluidState(pos.above()), this.fluid.get()) ? STABLE_SHAPE : Shapes.empty();
//    }
//
//    @Override
//    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
//        return !this.fluid.get().is(FluidTags.LAVA);
//    }
//
//    @Environment(EnvType.CLIENT)
//    @Override
//    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction direction) {
//        return adjacentBlockState.getFluidState().getType().isSame(this.fluid.get());
//    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (this.shouldSpreadLiquid(level, pos))
            level.getLiquidTicks().scheduleTick(pos, state.getFluidState().getType(), this.fluid.getTickDelay(level));
    }

//    @Override
//    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
//        if (state.getFluidState().isSource() || neighborState.getFluidState().isSource())
//            level.getLiquidTicks().scheduleTick(currentPos, state.getFluidState().getType(), this.fluid.get().getTickDelay(level));
//
//        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
//    }

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
                level.levelEvent(1501, pos, 0);
                return false;
            }
        }

        return true;
    }

//    @Override
//    public Fluid takeLiquid(LevelAccessor level, BlockPos pos, BlockState state) {
//        if (state.getValue(LEVEL) == 0) {
//            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
//            return this.fluid.get();
//        }
//
//        return Fluids.EMPTY;
//    }
}
