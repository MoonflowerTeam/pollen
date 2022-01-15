package gg.moonflower.pollen.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class DebugPollenFlowerPotRenderer implements BlockRenderer {

    private static final ResourceLocation CAT_OCELOT_LOCATION = new ResourceLocation("textures/entity/cat/ocelot.png");
    private static final OcelotModel<?> OCELOT_MODEL = new OcelotModel<>(0.0F);

    @Override
    public void render(Level level, BlockPos pos, DataContainer container, MultiBufferSource buffer, PoseStack matrixStack, float partialTicks, Camera camera, GameRenderer gameRenderer, LightTexture lightmap, Matrix4f projection, int packedLight, int packedOverlay) {
        OCELOT_MODEL.renderToBuffer(matrixStack, buffer.getBuffer(OCELOT_MODEL.renderType(CAT_OCELOT_LOCATION)), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
