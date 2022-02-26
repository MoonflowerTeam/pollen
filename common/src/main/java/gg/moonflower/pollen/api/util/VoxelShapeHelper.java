package gg.moonflower.pollen.api.util;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Contains simple, useful methods for creating a {@link VoxelShape} with provided {@link Direction}.
 *
 * @author Ocelot
 * @see VoxelShape
 * @since 1.0.0
 */
public final class VoxelShapeHelper {
    private VoxelShapeHelper() {
    }

    /**
     * Creates a rotated shape from an {@link Direction.Axis}. Everything is based on the negative axes facing positive (Ex. minX to maxX, minY to maxY, and minZ to maxZ).
     *
     * @param x1   The min x for the shape
     * @param y1   The min y for the shape
     * @param z1   The min z for the shape
     * @param x2   The max x for the shape
     * @param y2   The max y for the shape
     * @param z2   The max z for the shape
     * @param axis The axis to rotate on
     * @return The rotated box shape
     */
    public static VoxelShape makeCuboidShape(double x1, double y1, double z1, double x2, double y2, double z2, Direction.Axis axis) {
        return switch (axis) {
            case X -> makeCuboidShape(x1, y1, z1, x2, y2, z2, Direction.EAST);
            case Y -> makeCuboidShape(x1, y1, z1, x2, y2, z2, Direction.UP);
            case Z -> makeCuboidShape(x1, y1, z1, x2, y2, z2, Direction.SOUTH);
        };
    }

    /**
     * Creates a rotated shape from a {@link Direction}. Everything is based on the negative axes facing positive (Ex. minX to maxX, minY to maxY, and minZ to maxZ). Base facing direction is {@link Direction#NORTH}
     *
     * @param x1        The min x for the shape
     * @param y1        The min y for the shape
     * @param z1        The min z for the shape
     * @param x2        The max x for the shape
     * @param y2        The max y for the shape
     * @param z2        The max z for the shape
     * @param direction The direction to rotate towards
     * @return The rotated box shape
     */
    public static VoxelShape makeCuboidShape(double x1, double y1, double z1, double x2, double y2, double z2, Direction direction) {
        return switch (direction) {
            case UP -> Block.box(x1, z1, y1, x2, z2, y2);
            case DOWN -> Block.box(x1, 16 - z2, y1, x2, 16 - z1, y2);
            case NORTH -> Block.box(16 - x2, y1, 16 - z2, 16 - x1, y2, 16 - z1);
            case EAST -> Block.box(z1, y1, 16 - x2, z2, y2, 16 - x1);
            case SOUTH -> Block.box(x1, y1, z1, x2, y2, z2);
            case WEST -> Block.box(16 - z2, y1, x1, 16 - z1, y2, x2);
        };
    }

    /**
     * <p>Manages the combining of {@link VoxelShape} into a single complex shape.</p>
     *
     * @author Ocelot
     * @see VoxelShape
     * @since 2.0.0
     */
    public static final class Builder {
        private final Set<VoxelShape> shapes;

        public Builder() {
            this.shapes = new HashSet<>();
        }

        public Builder(Builder other) {
            this.shapes = new HashSet<>(other.shapes);
        }

        private Builder transformRaw(Function<AABB, VoxelShape> transformer) {
            Builder newBuilder = new Builder();
            for (VoxelShape shape : this.shapes) {
                Set<VoxelShape> rotatedShapes = new HashSet<>();
                for (AABB box : shape.toAabbs()) {
                    rotatedShapes.add(transformer.apply(box));
                }
                VoxelShape result = Shapes.empty();
                for (VoxelShape rotatedShape : rotatedShapes) {
                    result = Shapes.joinUnoptimized(result, rotatedShape, BooleanOp.OR);
                }
                newBuilder.append(result.optimize());
            }
            return newBuilder;
        }

        /**
         * Appends the specified shapes to the sets.
         *
         * @param shapes The shapes to add
         * @return The builder instance for chaining
         */
        public Builder append(VoxelShape... shapes) {
            this.shapes.addAll(Arrays.asList(shapes));
            return this;
        }

        /**
         * Appends the specified shapes to the sets.
         *
         * @param other The other builder with shapes to add
         * @return The builder instance for chaining
         */
        public Builder append(Builder other) {
            this.shapes.addAll(other.shapes);
            return this;
        }

        /**
         * Translates the entire shape in the specified direction.
         *
         * @param x The amount in the x direction to add
         * @param y The amount in the y direction to add
         * @param z The amount in the z direction to add
         * @return The translated builder
         */
        public Builder translate(double x, double y, double z) {
            return transformRaw(box -> Block.box(box.minX * 16.0 + x, box.minY * 16.0 + y, box.minZ * 16.0 + z, box.maxX * 16.0 + x, box.maxY * 16.0 + y, box.maxZ * 16.0 + z));
        }

        /**
         * Rotates the entire shape in the specified axis.
         *
         * @param axis The axis to rotate on
         * @return The rotated builder
         */
        public Builder rotate(Direction.Axis axis) {
            return transformRaw(box -> VoxelShapeHelper.makeCuboidShape(box.minX * 16.0, box.minY * 16.0, box.minZ * 16.0, box.maxX * 16.0, box.maxY * 16.0, box.maxZ * 16.0, axis));
        }

        /**
         * Rotates the entire shape in the specified direction.
         *
         * @param direction The direction to rotate on
         * @return The rotated builder
         */
        public Builder rotate(Direction direction) {
            return transformRaw(box -> VoxelShapeHelper.makeCuboidShape(box.minX * 16.0, box.minY * 16.0, box.minZ * 16.0, box.maxX * 16.0, box.maxY * 16.0, box.maxZ * 16.0, direction));
        }

        /**
         * Scales the entire shape in the specified directions.
         *
         * @param x The amount in the x direction to scale
         * @param y The amount in the y direction to scale
         * @param z The amount in the z direction to scale
         * @return The scaled builder
         */
        public Builder scale(double x, double y, double z) {
            return transformRaw(box -> Block.box(box.minX * 16.0 * x, box.minY * 16.0 * y, box.minZ * 16.0 * z, box.maxX * 16.0 * x, box.maxY * 16.0 * y, box.maxZ * 16.0 * z));
        }

        /**
         * @return A combined shape using {@link BooleanOp#OR}
         */
        public VoxelShape build() {
            return this.build(BooleanOp.OR);
        }

        /**
         * Combines the appended shapes into a single complex shape.
         *
         * @param combineFunction The function to use when combining the shapes together
         * @return A combined shape using the provided function
         */
        public VoxelShape build(BooleanOp combineFunction) {
            if (this.shapes.isEmpty())
                return Shapes.empty();
            VoxelShape result = Shapes.empty();
            for (VoxelShape shape : this.shapes) {
                result = Shapes.joinUnoptimized(result, shape, combineFunction);
            }
            return result.optimize();
        }
    }
}
