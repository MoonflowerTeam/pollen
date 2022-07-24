package gg.moonflower.pollen.api.datagen.provider.model;

import com.google.gson.JsonElement;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.blockstates.*;
import net.minecraft.data.models.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.*;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base implementation of {@link PollinatedModelGenerator} for blocks. Block states and models are created using the provided methods.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public abstract class PollinatedBlockModelGenerator implements PollinatedModelGenerator {

    private final Consumer<BlockStateGenerator> blockStateOutput;
    private final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;
    private final Consumer<Item> skippedAutoModelsOutput;

    public PollinatedBlockModelGenerator(Consumer<BlockStateGenerator> blockStateOutput, BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput, Consumer<Item> skippedAutoModelsOutput) {
        this.blockStateOutput = blockStateOutput;
        this.modelOutput = modelOutput;
        this.skippedAutoModelsOutput = skippedAutoModelsOutput;
    }

    protected static PropertyDispatch createHorizontalFacingDispatch() {
        return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Variant.variant());
    }

    protected static PropertyDispatch createHorizontalFacingDispatchAlt() {
        return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.SOUTH, Variant.variant()).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

    protected static PropertyDispatch createTorchHorizontalDispatch() {
        return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, Variant.variant()).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

    protected static PropertyDispatch createFacingDispatch() {
        return PropertyDispatch.property(BlockStateProperties.FACING).select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.UP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Variant.variant()).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
    }

    protected static MultiVariantGenerator createRotatedVariant(Block block, ResourceLocation modelLocation) {
        return MultiVariantGenerator.multiVariant(block, createRotatedVariants(modelLocation));
    }

    protected static Variant[] createRotatedVariants(ResourceLocation modelLocation) {
        return new Variant[]{Variant.variant().with(VariantProperties.MODEL, modelLocation), Variant.variant().with(VariantProperties.MODEL, modelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, modelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, modelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)};
    }

    protected static MultiVariantGenerator createRotatedVariant(Block block, ResourceLocation normalModelLocation, ResourceLocation mirroredModelLocation) {
        return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, normalModelLocation), Variant.variant().with(VariantProperties.MODEL, mirroredModelLocation), Variant.variant().with(VariantProperties.MODEL, normalModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, mirroredModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180));
    }

    protected static PropertyDispatch createBooleanModelDispatch(BooleanProperty property, ResourceLocation trueModelLocation, ResourceLocation falseModelLocation) {
        return PropertyDispatch.property(property).select(true, Variant.variant().with(VariantProperties.MODEL, trueModelLocation)).select(false, Variant.variant().with(VariantProperties.MODEL, falseModelLocation));
    }

    protected static BlockStateGenerator createButton(Block buttonBlock, ResourceLocation unpoweredModelLocation, ResourceLocation poweredModelLocation) {
        return MultiVariantGenerator.multiVariant(buttonBlock).with(PropertyDispatch.property(BlockStateProperties.POWERED).select(false, Variant.variant().with(VariantProperties.MODEL, unpoweredModelLocation)).select(true, Variant.variant().with(VariantProperties.MODEL, poweredModelLocation))).with(PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.FLOOR, Direction.NORTH, Variant.variant()).select(AttachFace.WALL, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.CEILING, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)));
    }

    protected static PropertyDispatch.C4<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> configureDoorHalf(PropertyDispatch.C4<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> c4, DoubleBlockHalf doubleBlockHalf, ResourceLocation resourceLocation, ResourceLocation resourceLocation2, ResourceLocation resourceLocation3, ResourceLocation resourceLocation4) {
        return c4.select(Direction.EAST, doubleBlockHalf, DoorHingeSide.LEFT, false, Variant.variant().with(VariantProperties.MODEL, resourceLocation)).select(Direction.SOUTH, doubleBlockHalf, DoorHingeSide.LEFT, false, Variant.variant().with(VariantProperties.MODEL, resourceLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, doubleBlockHalf, DoorHingeSide.LEFT, false, Variant.variant().with(VariantProperties.MODEL, resourceLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.NORTH, doubleBlockHalf, DoorHingeSide.LEFT, false, Variant.variant().with(VariantProperties.MODEL, resourceLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, doubleBlockHalf, DoorHingeSide.RIGHT, false, Variant.variant().with(VariantProperties.MODEL, resourceLocation3)).select(Direction.SOUTH, doubleBlockHalf, DoorHingeSide.RIGHT, false, Variant.variant().with(VariantProperties.MODEL, resourceLocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, doubleBlockHalf, DoorHingeSide.RIGHT, false, Variant.variant().with(VariantProperties.MODEL, resourceLocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.NORTH, doubleBlockHalf, DoorHingeSide.RIGHT, false, Variant.variant().with(VariantProperties.MODEL, resourceLocation3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, doubleBlockHalf, DoorHingeSide.LEFT, true, Variant.variant().with(VariantProperties.MODEL, resourceLocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, doubleBlockHalf, DoorHingeSide.LEFT, true, Variant.variant().with(VariantProperties.MODEL, resourceLocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, doubleBlockHalf, DoorHingeSide.LEFT, true, Variant.variant().with(VariantProperties.MODEL, resourceLocation2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, doubleBlockHalf, DoorHingeSide.LEFT, true, Variant.variant().with(VariantProperties.MODEL, resourceLocation2)).select(Direction.EAST, doubleBlockHalf, DoorHingeSide.RIGHT, true, Variant.variant().with(VariantProperties.MODEL, resourceLocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.SOUTH, doubleBlockHalf, DoorHingeSide.RIGHT, true, Variant.variant().with(VariantProperties.MODEL, resourceLocation4)).select(Direction.WEST, doubleBlockHalf, DoorHingeSide.RIGHT, true, Variant.variant().with(VariantProperties.MODEL, resourceLocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.NORTH, doubleBlockHalf, DoorHingeSide.RIGHT, true, Variant.variant().with(VariantProperties.MODEL, resourceLocation4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180));
    }

    protected static BlockStateGenerator createDoor(Block doorBlock, ResourceLocation resourceLocation, ResourceLocation resourceLocation2, ResourceLocation resourceLocation3, ResourceLocation resourceLocation4, ResourceLocation resourceLocation5, ResourceLocation resourceLocation6, ResourceLocation resourceLocation7, ResourceLocation resourceLocation8) {
        return MultiVariantGenerator
            .multiVariant(doorBlock)
            .with(configureDoorHalf(
                configureDoorHalf(PropertyDispatch
                    .properties(
                        BlockStateProperties.HORIZONTAL_FACING,
                        BlockStateProperties.DOUBLE_BLOCK_HALF,
                        BlockStateProperties.DOOR_HINGE,
                        BlockStateProperties.OPEN
                    ), DoubleBlockHalf.LOWER,
                    resourceLocation,
                    resourceLocation2,
                    resourceLocation3,
                    resourceLocation4
                ),
                DoubleBlockHalf.UPPER,
                resourceLocation5,
                resourceLocation6,
                resourceLocation7,
                resourceLocation8));
    }

    protected static BlockStateGenerator createFence(Block fenceBlock, ResourceLocation fencePostModelLocation, ResourceLocation fenceSideModelLocation) {
        return MultiPartGenerator.multiPart(fenceBlock).with(Variant.variant().with(VariantProperties.MODEL, fencePostModelLocation)).with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, fenceSideModelLocation).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, fenceSideModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, fenceSideModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, fenceSideModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true));
    }

    protected static BlockStateGenerator createWall(Block wallBlock, ResourceLocation postModelLocation, ResourceLocation lowSideModelLocation, ResourceLocation tallSideModelLocation) {
        return MultiPartGenerator.multiPart(wallBlock).with(Condition.condition().term(BlockStateProperties.UP, true), Variant.variant().with(VariantProperties.MODEL, postModelLocation)).with(Condition.condition().term(BlockStateProperties.NORTH_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, lowSideModelLocation).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, lowSideModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, lowSideModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, lowSideModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.NORTH_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, tallSideModelLocation).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, tallSideModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, tallSideModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with(Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, tallSideModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true));
    }

    protected static BlockStateGenerator createFenceGate(Block fenceGateBlock, ResourceLocation openModelLocation, ResourceLocation closedModelLocation, ResourceLocation wallOpenModelLocation, ResourceLocation wallClosedModelLocation) {
        return MultiVariantGenerator.multiVariant(fenceGateBlock, Variant.variant().with(VariantProperties.UV_LOCK, true)).with(createHorizontalFacingDispatchAlt()).with(PropertyDispatch.properties(BlockStateProperties.IN_WALL, BlockStateProperties.OPEN).select(false, false, Variant.variant().with(VariantProperties.MODEL, closedModelLocation)).select(true, false, Variant.variant().with(VariantProperties.MODEL, wallClosedModelLocation)).select(false, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation)).select(true, true, Variant.variant().with(VariantProperties.MODEL, wallOpenModelLocation)));
    }

    protected static BlockStateGenerator createStairs(Block stairsBlock, ResourceLocation innerModelLocation, ResourceLocation straightModelLocation, ResourceLocation outerModelLocation) {
        return MultiVariantGenerator.multiVariant(stairsBlock).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE).select(Direction.EAST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, straightModelLocation)).select(Direction.WEST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, straightModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, straightModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, straightModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation)).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation)).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation)).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation)).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, straightModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, straightModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, straightModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, straightModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, outerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, innerModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)));
    }

    protected static BlockStateGenerator createOrientableTrapdoor(Block orientableTrapdoorBlock, ResourceLocation topModelLocation, ResourceLocation bottomModelLocation, ResourceLocation openModelLocation) {
        return MultiVariantGenerator.multiVariant(orientableTrapdoorBlock).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, bottomModelLocation)).select(Direction.SOUTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, bottomModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, bottomModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, bottomModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, topModelLocation)).select(Direction.SOUTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, topModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, topModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, topModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation)).select(Direction.SOUTH, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.SOUTH, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R0)).select(Direction.EAST, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.WEST, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
    }

    protected static BlockStateGenerator createTrapdoor(Block trapdoorBlock, ResourceLocation topModelLocation, ResourceLocation bottomModelLocation, ResourceLocation openModelLocation) {
        return MultiVariantGenerator.multiVariant(trapdoorBlock).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, bottomModelLocation)).select(Direction.SOUTH, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, bottomModelLocation)).select(Direction.EAST, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, bottomModelLocation)).select(Direction.WEST, Half.BOTTOM, false, Variant.variant().with(VariantProperties.MODEL, bottomModelLocation)).select(Direction.NORTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, topModelLocation)).select(Direction.SOUTH, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, topModelLocation)).select(Direction.EAST, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, topModelLocation)).select(Direction.WEST, Half.TOP, false, Variant.variant().with(VariantProperties.MODEL, topModelLocation)).select(Direction.NORTH, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation)).select(Direction.SOUTH, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation)).select(Direction.SOUTH, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.TOP, true, Variant.variant().with(VariantProperties.MODEL, openModelLocation).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
    }

    protected static MultiVariantGenerator createSimpleBlock(Block block, ResourceLocation modelLocation) {
        return MultiVariantGenerator.multiVariant(block, Variant.variant().with(VariantProperties.MODEL, modelLocation));
    }

    protected static PropertyDispatch createRotatedPillar() {
        return PropertyDispatch.property(BlockStateProperties.AXIS).select(Direction.Axis.Y, Variant.variant()).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.Axis.X, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
    }

    protected static BlockStateGenerator createAxisAlignedPillarBlock(Block axisAlignedPillarBlock, ResourceLocation modelLocation) {
        return MultiVariantGenerator.multiVariant(axisAlignedPillarBlock, Variant.variant().with(VariantProperties.MODEL, modelLocation)).with(createRotatedPillar());
    }

    protected static BlockStateGenerator createRotatedPillarWithHorizontalVariant(Block rotatedPillarBlock, ResourceLocation modelLocation, ResourceLocation horizontalModelLocation) {
        return MultiVariantGenerator.multiVariant(rotatedPillarBlock).with(PropertyDispatch.property(BlockStateProperties.AXIS).select(Direction.Axis.Y, Variant.variant().with(VariantProperties.MODEL, modelLocation)).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, horizontalModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, horizontalModelLocation).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
    }

    protected static BlockStateGenerator createPressurePlate(Block pressurePlateBlock, ResourceLocation unpoweredModelLocation, ResourceLocation poweredModelLocation) {
        return MultiVariantGenerator.multiVariant(pressurePlateBlock).with(createBooleanModelDispatch(BlockStateProperties.POWERED, poweredModelLocation, unpoweredModelLocation));
    }

    protected static BlockStateGenerator createSlab(Block slabBlock, ResourceLocation bottomHalfModelLocation, ResourceLocation topHalfModelLocation, ResourceLocation doubleModelLocation) {
        return MultiVariantGenerator.multiVariant(slabBlock).with(PropertyDispatch.property(BlockStateProperties.SLAB_TYPE).select(SlabType.BOTTOM, Variant.variant().with(VariantProperties.MODEL, bottomHalfModelLocation)).select(SlabType.TOP, Variant.variant().with(VariantProperties.MODEL, topHalfModelLocation)).select(SlabType.DOUBLE, Variant.variant().with(VariantProperties.MODEL, doubleModelLocation)));
    }

    public Consumer<BlockStateGenerator> getBlockStateOutput() {
        return blockStateOutput;
    }

    public BiConsumer<ResourceLocation, Supplier<JsonElement>> getModelOutput() {
        return modelOutput;
    }

    public Consumer<Item> getSkippedAutoModelsOutput() {
        return skippedAutoModelsOutput;
    }

    protected void skipAutoItemBlock(Block block) {
        this.skippedAutoModelsOutput.accept(block.asItem());
    }

    protected void delegateItemModel(Block block, ResourceLocation delegateModelLocation) {
        this.modelOutput.accept(ModelLocationUtils.getModelLocation(block.asItem()), new DelegatedModel(delegateModelLocation));
    }

    protected void delegateItemModel(Item item, ResourceLocation delegateModelLocation) {
        this.modelOutput.accept(ModelLocationUtils.getModelLocation(item), new DelegatedModel(delegateModelLocation));
    }

    protected void createSimpleFlatItemModel(Item flatItem) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(flatItem), TextureMapping.layer0(flatItem), this.modelOutput);
    }

    protected void createSimpleFlatItemModel(Block flatBlock) {
        Item item = flatBlock.asItem();
        if (item != Items.AIR)
            ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(item), TextureMapping.layer0(flatBlock), this.modelOutput);
    }

    protected void createSimpleFlatItemModel(Block flatBlock, String layerZeroTextureSuffix) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(flatBlock.asItem()), TextureMapping.layer0(TextureMapping.getBlockTexture(flatBlock, layerZeroTextureSuffix)), this.modelOutput);
    }

    protected void createRotatedMirroredVariantBlock(Block block) {
        ResourceLocation resourceLocation = TexturedModel.CUBE.create(block, this.modelOutput);
        ResourceLocation resourceLocation2 = TexturedModel.CUBE_MIRRORED.create(block, this.modelOutput);
        this.blockStateOutput.accept(createRotatedVariant(block, resourceLocation, resourceLocation2));
    }

    protected void createRotatedVariantBlock(Block block) {
        ResourceLocation resourceLocation = TexturedModel.CUBE.create(block, this.modelOutput);
        this.blockStateOutput.accept(createRotatedVariant(block, resourceLocation));
    }

    protected void createAxisAlignedPillarBlockCustomModel(Block axisAlignedPillarBlock, ResourceLocation modelLocation) {
        this.blockStateOutput.accept(createAxisAlignedPillarBlock(axisAlignedPillarBlock, modelLocation));
    }

    protected void createAxisAlignedPillarBlock(Block axisAlignedPillarBlock, TexturedModel.Provider provider) {
        ResourceLocation resourceLocation = provider.create(axisAlignedPillarBlock, this.modelOutput);
        this.blockStateOutput.accept(createAxisAlignedPillarBlock(axisAlignedPillarBlock, resourceLocation));
    }

    protected void createHorizontallyRotatedBlock(Block horizontallyRotatedBlock, TexturedModel.Provider provider) {
        ResourceLocation resourceLocation = provider.create(horizontallyRotatedBlock, this.modelOutput);
        this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(horizontallyRotatedBlock, Variant.variant().with(VariantProperties.MODEL, resourceLocation)).with(createHorizontalFacingDispatch()));
    }

    protected void createRotatedPillarWithHorizontalVariant(Block rotatedPillarBlock, TexturedModel.Provider modelProvider, TexturedModel.Provider horizontalModelProvider) {
        ResourceLocation resourceLocation = modelProvider.create(rotatedPillarBlock, this.modelOutput);
        ResourceLocation resourceLocation2 = horizontalModelProvider.create(rotatedPillarBlock, this.modelOutput);
        this.blockStateOutput.accept(createRotatedPillarWithHorizontalVariant(rotatedPillarBlock, resourceLocation, resourceLocation2));
    }

    protected ResourceLocation createSuffixedVariant(Block block, String suffix, ModelTemplate modelTemplate, Function<ResourceLocation, TextureMapping> textureMappingGetter) {
        return modelTemplate.createWithSuffix(block, suffix, textureMappingGetter.apply(TextureMapping.getBlockTexture(block, suffix)), this.modelOutput);
    }

    protected void createTrivialCube(Block block) {
        this.createTrivialBlock(block, TexturedModel.CUBE);
    }

    protected void createTrivialBlock(Block block, TexturedModel.Provider provider) {
        this.blockStateOutput.accept(createSimpleBlock(block, provider.create(block, this.modelOutput)));
    }

    protected void createTrivialBlock(Block block, TextureMapping textureMapping, ModelTemplate modelTemplate) {
        ResourceLocation resourceLocation = modelTemplate.create(block, textureMapping, this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(block, resourceLocation));
    }

    protected PollinatedBlockModelGenerator.BlockFamilyProvider family(Block block, TexturedModel texturedModel) {
        return (new PollinatedBlockModelGenerator.BlockFamilyProvider(texturedModel.getMapping())).fullBlock(block, texturedModel.getTemplate());
    }

    protected PollinatedBlockModelGenerator.BlockFamilyProvider family(Block block, TexturedModel.Provider provider) {
        TexturedModel texturedModel = provider.get(block);
        return (new PollinatedBlockModelGenerator.BlockFamilyProvider(texturedModel.getMapping())).fullBlock(block, texturedModel.getTemplate());
    }

    protected PollinatedBlockModelGenerator.BlockFamilyProvider family(Block block) {
        return this.family(block, TexturedModel.CUBE);
    }

    protected PollinatedBlockModelGenerator.BlockFamilyProvider family(TextureMapping textureMapping) {
        return new PollinatedBlockModelGenerator.BlockFamilyProvider(textureMapping);
    }

    protected void createDoor(Block doorBlock) {

        TextureMapping textureMapping = TextureMapping.door(doorBlock);
        ResourceLocation resourceLocation = ModelTemplates.DOOR_BOTTOM_LEFT.create(doorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation2 = ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create(doorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation3 = ModelTemplates.DOOR_BOTTOM_RIGHT.create(doorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation4 = ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create(doorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation5 = ModelTemplates.DOOR_TOP_LEFT.create(doorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation6 = ModelTemplates.DOOR_TOP_LEFT_OPEN.create(doorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation7 = ModelTemplates.DOOR_TOP_RIGHT.create(doorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation8 = ModelTemplates.DOOR_TOP_RIGHT_OPEN.create(doorBlock, textureMapping, this.modelOutput);
        this.createSimpleFlatItemModel(doorBlock.asItem());
        this.blockStateOutput.accept(createDoor(doorBlock, resourceLocation, resourceLocation2, resourceLocation3, resourceLocation4, resourceLocation5, resourceLocation6, resourceLocation7, resourceLocation8));
    }

    protected void createOrientableTrapdoor(Block orientableTrapdoorBlock) {
        TextureMapping textureMapping = TextureMapping.defaultTexture(orientableTrapdoorBlock);
        ResourceLocation resourceLocation = ModelTemplates.ORIENTABLE_TRAPDOOR_TOP.create(orientableTrapdoorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation2 = ModelTemplates.ORIENTABLE_TRAPDOOR_BOTTOM.create(orientableTrapdoorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation3 = ModelTemplates.ORIENTABLE_TRAPDOOR_OPEN.create(orientableTrapdoorBlock, textureMapping, this.modelOutput);
        this.blockStateOutput.accept(createOrientableTrapdoor(orientableTrapdoorBlock, resourceLocation, resourceLocation2, resourceLocation3));
        this.delegateItemModel(orientableTrapdoorBlock, resourceLocation2);
    }

    protected void createTrapdoor(Block trapdoorBlock) {
        TextureMapping textureMapping = TextureMapping.defaultTexture(trapdoorBlock);
        ResourceLocation resourceLocation = ModelTemplates.TRAPDOOR_TOP.create(trapdoorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation2 = ModelTemplates.TRAPDOOR_BOTTOM.create(trapdoorBlock, textureMapping, this.modelOutput);
        ResourceLocation resourceLocation3 = ModelTemplates.TRAPDOOR_OPEN.create(trapdoorBlock, textureMapping, this.modelOutput);
        this.blockStateOutput.accept(createTrapdoor(trapdoorBlock, resourceLocation, resourceLocation2, resourceLocation3));
        this.delegateItemModel(trapdoorBlock, resourceLocation2);
    }

    protected PollinatedBlockModelGenerator.WoodProvider woodProvider(Block logBlock) {
        return new PollinatedBlockModelGenerator.WoodProvider(TextureMapping.logColumn(logBlock));
    }

    protected void createNonTemplateModelBlock(Block block) {
        this.createNonTemplateModelBlock(block, block);
    }

    protected void createNonTemplateModelBlock(Block block, Block modelBlock) {
        this.blockStateOutput.accept(createSimpleBlock(block, ModelLocationUtils.getModelLocation(modelBlock)));
    }

    protected void createCrossBlockWithDefaultItem(Block crossBlock, BlockModelGenerators.TintState tintState) {
        this.createSimpleFlatItemModel(crossBlock);
        this.createCrossBlock(crossBlock, tintState);
    }

    protected void createCrossBlockWithDefaultItem(Block crossBlock, BlockModelGenerators.TintState tintState, TextureMapping textureMapping) {
        this.createSimpleFlatItemModel(crossBlock);
        this.createCrossBlock(crossBlock, tintState, textureMapping);
    }

    protected void createCrossBlock(Block crossBlock, BlockModelGenerators.TintState tintState) {
        TextureMapping textureMapping = TextureMapping.cross(crossBlock);
        this.createCrossBlock(crossBlock, tintState, textureMapping);
    }

    protected void createCrossBlock(Block crossBlock, BlockModelGenerators.TintState tintState, TextureMapping textureMapping) {
        ResourceLocation resourceLocation = tintState.getCross().create(crossBlock, textureMapping, this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(crossBlock, resourceLocation));
    }

    protected void createPlant(Block plantBlock, Block pottedPlantBlock, BlockModelGenerators.TintState tintState) {
        this.createCrossBlockWithDefaultItem(plantBlock, tintState);
        TextureMapping textureMapping = TextureMapping.plant(plantBlock);
        ResourceLocation resourceLocation = tintState.getCrossPot().create(pottedPlantBlock, textureMapping, this.modelOutput);
        this.blockStateOutput.accept(createSimpleBlock(pottedPlantBlock, resourceLocation));
    }

    protected void copyModel(Block sourceBlock, Block targetBlock) {
        ResourceLocation resourceLocation = ModelLocationUtils.getModelLocation(sourceBlock);
        this.blockStateOutput.accept(MultiVariantGenerator.multiVariant(targetBlock, Variant.variant().with(VariantProperties.MODEL, resourceLocation)));
        this.delegateItemModel(targetBlock, resourceLocation);
    }

    public class BlockEntityModelGenerator {

        private final ResourceLocation baseModel;

        public BlockEntityModelGenerator(ResourceLocation resourceLocation, Block block) {
            this.baseModel = ModelTemplates.PARTICLE_ONLY.create(resourceLocation, TextureMapping.particle(block), PollinatedBlockModelGenerator.this.modelOutput);
        }

        public PollinatedBlockModelGenerator.BlockEntityModelGenerator create(Block... blocks) {
            for (Block block : blocks)
                PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createSimpleBlock(block, this.baseModel));
            return this;
        }

        public PollinatedBlockModelGenerator.BlockEntityModelGenerator createWithoutBlockItem(Block... blocks) {
            for (Block block : blocks)
                PollinatedBlockModelGenerator.this.skipAutoItemBlock(block);
            return this.create(blocks);
        }

        public PollinatedBlockModelGenerator.BlockEntityModelGenerator createWithCustomBlockItemModel(ModelTemplate modelTemplate, Block... blocks) {
            for (Block block : blocks)
                modelTemplate.create(ModelLocationUtils.getModelLocation(block.asItem()), TextureMapping.particle(block), PollinatedBlockModelGenerator.this.modelOutput);
            return this.create(blocks);
        }
    }

    public class BlockFamilyProvider {

        private final TextureMapping mapping;
        @Nullable
        private ResourceLocation fullBlock;

        private BlockFamilyProvider(TextureMapping textureMapping) {
            this.mapping = textureMapping;
        }

        public PollinatedBlockModelGenerator.BlockFamilyProvider fullBlock(Block block, ModelTemplate modelTemplate) {
            this.fullBlock = modelTemplate.create(block, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createSimpleBlock(block, this.fullBlock));
            return this;
        }

        public PollinatedBlockModelGenerator.BlockFamilyProvider fullBlock(Function<TextureMapping, ResourceLocation> function) {
            this.fullBlock = function.apply(this.mapping);
            return this;
        }

        public PollinatedBlockModelGenerator.BlockFamilyProvider button(Block buttonBlock) {
            ResourceLocation resourceLocation = ModelTemplates.BUTTON.create(buttonBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.BUTTON_PRESSED.create(buttonBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createButton(buttonBlock, resourceLocation, resourceLocation2));
            ResourceLocation resourceLocation3 = ModelTemplates.BUTTON_INVENTORY.create(buttonBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.delegateItemModel(buttonBlock, resourceLocation3);
            return this;
        }

        public PollinatedBlockModelGenerator.BlockFamilyProvider wall(Block wallBlock) {
            ResourceLocation resourceLocation = ModelTemplates.WALL_POST.create(wallBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.WALL_LOW_SIDE.create(wallBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation3 = ModelTemplates.WALL_TALL_SIDE.create(wallBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createWall(wallBlock, resourceLocation, resourceLocation2, resourceLocation3));
            ResourceLocation resourceLocation4 = ModelTemplates.WALL_INVENTORY.create(wallBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.delegateItemModel(wallBlock, resourceLocation4);
            return this;
        }

        public PollinatedBlockModelGenerator.BlockFamilyProvider fence(Block fenceBlock) {
            ResourceLocation resourceLocation = ModelTemplates.FENCE_POST.create(fenceBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.FENCE_SIDE.create(fenceBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createFence(fenceBlock, resourceLocation, resourceLocation2));
            ResourceLocation resourceLocation3 = ModelTemplates.FENCE_INVENTORY.create(fenceBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.delegateItemModel(fenceBlock, resourceLocation3);
            return this;
        }

        public PollinatedBlockModelGenerator.BlockFamilyProvider fenceGate(Block fenceGateBlock) {
            ResourceLocation resourceLocation = ModelTemplates.FENCE_GATE_OPEN.create(fenceGateBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.FENCE_GATE_CLOSED.create(fenceGateBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation3 = ModelTemplates.FENCE_GATE_WALL_OPEN.create(fenceGateBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation4 = ModelTemplates.FENCE_GATE_WALL_CLOSED.create(fenceGateBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createFenceGate(fenceGateBlock, resourceLocation, resourceLocation2, resourceLocation3, resourceLocation4));
            return this;
        }

        public PollinatedBlockModelGenerator.BlockFamilyProvider pressurePlate(Block pressurePlateBlock) {
            ResourceLocation resourceLocation = ModelTemplates.PRESSURE_PLATE_UP.create(pressurePlateBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.PRESSURE_PLATE_DOWN.create(pressurePlateBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createPressurePlate(pressurePlateBlock, resourceLocation, resourceLocation2));
            return this;
        }

        public PollinatedBlockModelGenerator.BlockFamilyProvider sign(Block block, Block block2) {
            ResourceLocation resourceLocation = ModelTemplates.PARTICLE_ONLY.create(block, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createSimpleBlock(block, resourceLocation));
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createSimpleBlock(block2, resourceLocation));
            PollinatedBlockModelGenerator.this.createSimpleFlatItemModel(block.asItem());
            PollinatedBlockModelGenerator.this.skipAutoItemBlock(block2);
            return this;
        }

        public PollinatedBlockModelGenerator.BlockFamilyProvider slab(Block slabBlock) {
            if (this.fullBlock == null) {
                throw new IllegalStateException("Full block not generated yet");
            } else {
                ResourceLocation resourceLocation = ModelTemplates.SLAB_BOTTOM.create(slabBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
                ResourceLocation resourceLocation2 = ModelTemplates.SLAB_TOP.create(slabBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
                PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createSlab(slabBlock, resourceLocation, resourceLocation2, this.fullBlock));
                return this;
            }
        }

        public PollinatedBlockModelGenerator.BlockFamilyProvider stairs(Block stairsBlock) {
            ResourceLocation resourceLocation = ModelTemplates.STAIRS_INNER.create(stairsBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.STAIRS_STRAIGHT.create(stairsBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation3 = ModelTemplates.STAIRS_OUTER.create(stairsBlock, this.mapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createStairs(stairsBlock, resourceLocation, resourceLocation2, resourceLocation3));
            return this;
        }
    }

    public class WoodProvider {

        private final TextureMapping logMapping;

        private WoodProvider(TextureMapping textureMapping) {
            this.logMapping = textureMapping;
        }

        public PollinatedBlockModelGenerator.WoodProvider wood(Block woodBlock) {
            TextureMapping textureMapping = this.logMapping.copyAndUpdate(TextureSlot.END, this.logMapping.get(TextureSlot.SIDE));
            ResourceLocation resourceLocation = ModelTemplates.CUBE_COLUMN.create(woodBlock, textureMapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createAxisAlignedPillarBlock(woodBlock, resourceLocation));
            return this;
        }

        public PollinatedBlockModelGenerator.WoodProvider log(Block logBlock) {
            ResourceLocation resourceLocation = ModelTemplates.CUBE_COLUMN.create(logBlock, this.logMapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createAxisAlignedPillarBlock(logBlock, resourceLocation));
            return this;
        }

        public PollinatedBlockModelGenerator.WoodProvider logWithHorizontal(Block logBlock) {
            ResourceLocation resourceLocation = ModelTemplates.CUBE_COLUMN.create(logBlock, this.logMapping, PollinatedBlockModelGenerator.this.modelOutput);
            ResourceLocation resourceLocation2 = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(logBlock, this.logMapping, PollinatedBlockModelGenerator.this.modelOutput);
            PollinatedBlockModelGenerator.this.blockStateOutput.accept(PollinatedBlockModelGenerator.createRotatedPillarWithHorizontalVariant(logBlock, resourceLocation, resourceLocation2));
            return this;
        }
    }
}
