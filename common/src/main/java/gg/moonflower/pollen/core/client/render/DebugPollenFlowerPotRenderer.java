package gg.moonflower.pollen.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Random;

public class DebugPollenFlowerPotRenderer implements BlockRenderer {

    private static final RandomSource RANDOM = RandomSource.create();

    @Override
    public void render(Level level, BlockPos pos, DataContainer container, MultiBufferSource buffer, PoseStack matrixStack, float partialTicks, Camera camera, GameRenderer gameRenderer, LightTexture lightmap, Matrix4f projection, int packedLight, int packedOverlay) {
//        RANDOM.setSeed(pos.asLong());
        List<BlockState> possibleStates = Registry.BLOCK.getRandom(RANDOM).map(Holder::value).orElse(Blocks.AIR).getStateDefinition().getPossibleStates();
        BlockState state = possibleStates.get(RANDOM.nextInt(possibleStates.size()));
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(state, matrixStack, buffer, packedLight, packedOverlay);
    }
}
