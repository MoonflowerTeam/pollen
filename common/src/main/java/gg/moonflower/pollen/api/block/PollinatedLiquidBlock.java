package gg.moonflower.pollen.api.block;

import com.google.common.base.Suppliers;
import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;

/**
 * A flowing liquid block for modded fluids.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class PollinatedLiquidBlock extends Block implements BucketPickup {

    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
    public static final VoxelShape STABLE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    private final Supplier<FlowingFluid> fluid;
    private final Supplier<List<FluidState>> stateCache;

    public PollinatedLiquidBlock(Supplier<FlowingFluid> fluid, BlockBehaviour.Properties properties) {
        super(properties);
        this.fluid = fluid;
        this.stateCache = Suppliers.memoize(() -> {
            FlowingFluid flowingFluid = fluid.get();
            List<FluidState> list = new ArrayList<>();
            list.add(flowingFluid.getSource(false));

            for (int i = 1; i < 8; ++i) {
                list.add(flowingFluid.getFlowing(8 - i, false));
            }

            list.add(flowingFluid.getFlowing(8, true));
            return list;
        });
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    public PollinatedLiquidBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
        this(() -> fluid, properties);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return context.isAbove(STABLE_SHAPE, pos, true) && state.getValue(LEVEL) == 0 && context.canStandOnFluid(level.getFluidState(pos.above()), state.getFluidState()) ? STABLE_SHAPE : Shapes.empty();
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return state.getFluidState().isRandomlyTicking();
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        state.getFluidState().randomTick(level, pos, random);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return false;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
        return !this.getFluid().is(FluidTags.LAVA);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return this.stateCache.get().get(Math.min(state.getValue(LEVEL), 8));
    }

    @Environment(EnvType.CLIENT)
    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction direction) {
        return adjacentBlockState.getFluidState().getType().isSame(this.getFluid());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        return Collections.emptyList();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (this.shouldSpreadLiquid(level, pos))
            level.scheduleTick(pos, state.getFluidState().getType(), this.getFluid().getTickDelay(level));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (state.getFluidState().isSource() || neighborState.getFluidState().isSource())
            level.scheduleTick(currentPos, state.getFluidState().getType(), this.getFluid().getTickDelay(level));

        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (this.shouldSpreadLiquid(level, pos))
            level.scheduleTick(pos, state.getFluidState().getType(), this.getFluid().getTickDelay(level));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        if (state.getValue(LEVEL) == 0) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
            return new ItemStack(this.getFluid().getBucket());
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return this.getFluid().getPickupSound();
    }

    public FlowingFluid getFluid() {
        return this.fluid.get();
    }

    protected boolean shouldSpreadLiquid(Level level, BlockPos pos) {
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
