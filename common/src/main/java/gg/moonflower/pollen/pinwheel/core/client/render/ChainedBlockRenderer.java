package gg.moonflower.pollen.pinwheel.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockData;
import gg.moonflower.pollen.pinwheel.api.client.blockdata.BlockDataKey;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.TickableBlockRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.Random;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ChainedBlockRenderer implements TickableBlockRenderer {

    private static final Random RANDOM = new Random();
    private static final BlockPos.MutableBlockPos CHAIN_POS = new BlockPos.MutableBlockPos();
    private static final BlockDataKey<Integer> TIME = BlockDataKey.of(() -> RANDOM.nextInt(Short.MAX_VALUE)).setBlocks(Blocks.CHAIN, Blocks.LANTERN, Blocks.SOUL_LANTERN).build();
    private static final BlockDataKey<BlockPos> TOP = BlockDataKey.<BlockPos>of(() -> null).setBlocks(Blocks.CHAIN, Blocks.LANTERN, Blocks.SOUL_LANTERN).build();
    private static final BlockDataKey<Boolean> ATTACHED = BlockDataKey.of(() -> false).setBlocks(Blocks.CHAIN, Blocks.LANTERN, Blocks.SOUL_LANTERN).build();

    @Override
    public void tick(Level level, BlockPos pos, BlockRenderer.DataContainer container) {
        BlockData<Integer> time = container.get(TIME);
        BlockData<Boolean> attached = container.get(ATTACHED);
        BlockData<BlockPos> top = container.get(TOP);
        time.set(time.get() + 1);

        CHAIN_POS.set(pos).move(Direction.DOWN);
        if (!level.getBlockState(CHAIN_POS).getBlock().is(Blocks.CHAIN)) {
            boolean shouldAttach = level.getBlockState(pos).is(Blocks.CHAIN) && Block.canSupportCenter(level, CHAIN_POS, Direction.UP);
            if (!attached.get().equals(shouldAttach)) {
                attached.set(shouldAttach);
                if (level.getBlockState(CHAIN_POS.set(pos).move(Direction.UP)).is(Blocks.CHAIN))
                    container.updateNeighbor(CHAIN_POS);
            }
        }

        CHAIN_POS.set(pos).move(Direction.UP);
        if (!level.getBlockState(CHAIN_POS).getBlock().is(Blocks.CHAIN) && !Objects.equals(top.get(), pos)) {
            top.set(pos);
            if (level.getBlockState(CHAIN_POS.set(pos).move(Direction.DOWN)).is(Blocks.CHAIN))
                container.updateNeighbor(CHAIN_POS);
        }
    }

    @Override
    public void receiveUpdate(Level level, BlockPos pos, BlockState oldState, BlockState newState, DataContainer container) {
        BlockData<BlockPos> top = container.get(TOP);
        BlockData<Boolean> attached = container.get(ATTACHED);

        CHAIN_POS.set(pos).move(Direction.DOWN);
        if (level.getBlockState(CHAIN_POS).getBlock().is(Blocks.CHAIN) && !attached.get().equals(container.get(ATTACHED, CHAIN_POS).get())) {
            attached.set(container.get(ATTACHED, CHAIN_POS).get());
            container.updateNeighbor(CHAIN_POS.set(pos).move(Direction.UP));
        }

        CHAIN_POS.set(pos).move(Direction.UP);
        if (!level.getBlockState(CHAIN_POS).getBlock().is(Blocks.CHAIN)) {
            top.set(pos);
            container.updateNeighbor(CHAIN_POS.set(pos).move(Direction.DOWN));
        } else if (level.getBlockState(CHAIN_POS).getBlock().is(Blocks.CHAIN)) {
            top.set(container.get(TOP, CHAIN_POS).get());
            container.updateNeighbor(CHAIN_POS.set(pos).move(Direction.DOWN));
        }
    }

    @Override
    public void render(Level level, BlockPos pos, DataContainer container, MultiBufferSource buffer, PoseStack matrixStack, float partialTicks, Camera camera, GameRenderer gameRenderer, LightTexture lightmap, Matrix4f projection, int packedLight, int packedOverlay) {
        if (!container.get(ATTACHED).get()) {
            BlockPos top = container.get(TOP).get() != null && level.getBlockState(container.get(TOP).get()).is(Blocks.CHAIN) ? container.get(TOP).get() : pos;
            int distance = top.getY() - pos.getY() + 1;
            float time = container.get(TIME, top).get() + partialTicks;
            matrixStack.translate(0.5, distance, 0.5);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(Mth.sin(time / 20.0F)));
            matrixStack.translate(-0.5, -distance, -0.5);
        }
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(level.getBlockState(pos), matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        Block block = state.getBlock();
        if (block.is(Blocks.CHAIN) && state.getValue(ChainBlock.AXIS) == Direction.Axis.Y)
            return RenderShape.INVISIBLE;
        return (block.is(Blocks.LANTERN) || block.is(Blocks.SOUL_LANTERN)) && state.getValue(Lantern.HANGING) ? RenderShape.INVISIBLE : RenderShape.MODEL;
    }
}
