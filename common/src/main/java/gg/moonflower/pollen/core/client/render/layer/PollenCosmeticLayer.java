package gg.moonflower.pollen.core.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import gg.moonflower.pollen.core.client.entitlement.RenderableCosmetic;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModel;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelManager;
import gg.moonflower.pollen.pinwheel.api.client.geometry.GeometryModelRenderer;
import gg.moonflower.pollen.pinwheel.api.client.texture.GeometryTextureManager;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/**
 * @author Ocelot
 */
public class PollenCosmeticLayer<T extends LivingEntity> extends RenderLayer<T, PlayerModel<T>> {

    public PollenCosmeticLayer(RenderLayerParent<T, PlayerModel<T>> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible() || GeometryTextureManager.isReloading() || GeometryModelManager.isReloading())
            return;

        EntitlementManager.getEntitlements(entity.getUUID()).forEach(entitlement -> {
            if (entitlement instanceof RenderableCosmetic) {
                RenderableCosmetic cosmetic = (RenderableCosmetic) entitlement;
                if (!cosmetic.isEnabled())
                    return;

                ResourceLocation modelName = cosmetic.getModelKey();
                if (modelName == null)
                    return;

                GeometryModel model = GeometryModelManager.getModel(modelName);
                ResourceLocation textureKey = cosmetic.getTextureKey();
                if (model == GeometryModel.EMPTY || textureKey == null)
                    return;

                GeometryModelRenderer.copyModelAngles(this.getParentModel(), model);
                GeometryModelRenderer.render(model, textureKey, buffer, matrixStack, packedLight, OverlayTexture.NO_OVERLAY, cosmetic.getRed(), cosmetic.getGreen(), cosmetic.getBlue(), cosmetic.getAlpha());
            }
        });
    }
}
