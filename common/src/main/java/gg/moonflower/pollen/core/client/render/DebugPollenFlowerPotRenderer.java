package gg.moonflower.pollen.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class DebugPollenFlowerPotRenderer implements BlockRenderer {

    private static final ResourceLocation CAT_OCELOT_LOCATION = new ResourceLocation("textures/entity/cat/ocelot.png");
    private static final OcelotModel<Entity> OCELOT_MODEL = new OcelotModel<>(0.0F);

    @Override
    public void render(Level level, BlockPos pos, DataContainer container, MultiBufferSource buffer, PoseStack matrixStack, float partialTicks, Camera camera, GameRenderer gameRenderer, LightTexture lightmap, Matrix4f projection, int packedLight, int packedOverlay) {
        Entity entity = Minecraft.getInstance().cameraEntity;
        if (entity == null)
            return;
        matrixStack.pushPose();
        matrixStack.translate(0.5, 1.501F, 0.5);
        matrixStack.scale(-1, -1, 1);
        OCELOT_MODEL.attackTime = 0;
        OCELOT_MODEL.riding = false;
        OCELOT_MODEL.young = false;
        OCELOT_MODEL.prepareMobModel(entity, 0, 0, partialTicks);
        OCELOT_MODEL.setupAnim(entity, 0, 0, entity.tickCount + partialTicks, 0, 0);
        OCELOT_MODEL.renderToBuffer(matrixStack, buffer.getBuffer(OCELOT_MODEL.renderType(CAT_OCELOT_LOCATION)), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStack.popPose();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
