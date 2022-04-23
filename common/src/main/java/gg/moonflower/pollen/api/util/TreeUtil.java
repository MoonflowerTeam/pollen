package gg.moonflower.pollen.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;

import java.util.Random;

/**
 * Utilities for creating a tree feature.
 *
 * @author JustinPlayzz
 * @author Steven
 * @author ebo2022
 */
public final class TreeUtil {

    /**
     * Places a directional log using the specified parameters.
     *
     * @param level     The level to place the log in
     * @param pos       The position to place the log at
     * @param direction The direction the log is facing
     * @param rand      An instance of {@link Random}
     * @param config    The feature configuration to fetch block state(s) from
     */
    public static void placeDirectionalLogAt(LevelWriter level, BlockPos pos, Direction direction, Random rand, TreeConfiguration config) {
        setForcedState(level, pos, config.trunkProvider.getState(rand, pos).setValue(RotatedPillarBlock.AXIS, direction.getAxis()));
    }

    /**
     * Places leaves using the specified parameters.
     *
     * @param world The level to place the leaves in
     * @param pos The position to place the leaves at
     * @param rand An instance of {@link Random}
     * @param config The feature configuration to fetch block state(s) from
     */
    public static void placeLeafAt(LevelSimulatedRW world, BlockPos pos, Random rand, TreeConfiguration config) {
        if (isAirOrLeaves(world, pos)) {
            setForcedState(world, pos, config.leavesProvider.getState(rand, pos).setValue(LeavesBlock.DISTANCE, 1));
        }
    }

    /**
     * Places the specified {@link BlockState} using the specified parameters.
     *
     * @param world The level to place the block in
     * @param pos   The position to place the leaves at
     * @param state The {@link BlockState} to place
     */
    public static void setForcedState(LevelWriter world, BlockPos pos, BlockState state) {
        world.setBlock(pos, state, 18);
    }

    /**
     * Checks whether there is air at the specified position.
     *
     * @param level The level to check for air in
     * @param pos   The position to check for air at
     * @return Whether there is air at the specified position
     */
    public static boolean isAir(LevelSimulatedReader level, BlockPos pos) {
        if (!(level instanceof BlockGetter)) {
            return level.isStateAtPosition(pos, BlockState::isAir);
        } else {
            return level.isStateAtPosition(pos, BlockBehaviour.BlockStateBase::isAir);
        }
    }

    /**
     * Checks whether there is air or leaves at the specified position.
     *
     * @param level The level to check for air or leaves in
     * @param pos   The position to check for air or leaves at
     * @return Whether there is air or leaves at the specified position
     */
    public static boolean isAirOrLeaves(LevelSimulatedReader level, BlockPos pos) {
        if (level instanceof LevelReader) {
            return level.isStateAtPosition(pos, state -> state.isAir() || state.is(BlockTags.LEAVES));
        }
         return level.isStateAtPosition(pos, (state) -> isAir(level, pos) || state.is(BlockTags.LEAVES));
    }

    /**
     * Sets dirt at the specified position.
     *
     * @param level The level to place dirt in
     * @param pos The position to place dirt at
     */
    public static void setDirtAt(LevelAccessor level, BlockPos pos) {
        Block block = level.getBlockState(pos).getBlock();
        if (block == Blocks.GRASS_BLOCK || block == Blocks.FARMLAND) {
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 18);
        }
    }

    /**
     * Checks whether the specified position is a valid block for a tree to grow on.
     *
     * @param level The level to check the specified position
     * @param pos   The position to check for valid ground
     * @return Whether the specified position is a valid block for a tree to grow on
     */
    public static boolean isValidGround(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.PODZOL) || state.is(Blocks.FARMLAND);
    }
}
