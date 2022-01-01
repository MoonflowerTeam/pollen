package gg.moonflower.pollen.pinwheel.core.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import gg.moonflower.pollen.core.Pollen;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModel;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.BeeModel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class FlowerPotBlockRenderer implements BlockRenderer {

    private static final BeeModel<Bee> BEE_MODEL = new BeeModel<>();
    private static final GeometryModel MODEL = GeometryModel.create(BEE_MODEL.texWidth, BEE_MODEL.texHeight, BEE_MODEL);
    private static final ResourceLocation TEXTURE = new ResourceLocation(Pollen.MOD_ID, "test_bee");

    @Override
    public void render(Level level, BlockPos pos, DataContainer container, MultiBufferSource buffer, PoseStack matrixStack, float partialTicks, Camera camera, GameRenderer gameRenderer, LightTexture lightmap, Matrix4f projection, int packedLight, int packedOverlay) {
        if (Minecraft.getInstance().player.isShiftKeyDown()) {
            BEE_MODEL.renderToBuffer(matrixStack, buffer.getBuffer(BEE_MODEL.renderType(new ResourceLocation("textures/entity/bee/bee.png"))), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        } else {
            GeometryModelRenderer.render(GeometryModel.create(BEE_MODEL.texWidth, BEE_MODEL.texHeight, BEE_MODEL), TEXTURE, matrixStack, packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}
