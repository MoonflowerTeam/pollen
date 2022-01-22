package gg.moonflower.pollen.core.client.render.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.core.client.entitlement.Cosmetic;
import gg.moonflower.pollen.core.client.entitlement.EntitlementManager;
import gg.moonflower.pollen.core.client.entitlement.ModelEntitlement;
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
        if (GeometryTextureManager.isReloading() || GeometryModelManager.isReloading())
            return;

        EntitlementManager.getEntitlements(entity.getUUID()).filter(entitlement -> entitlement instanceof Cosmetic && ((Cosmetic) entitlement).isEnabled()).forEach(entitlement -> {
            ResourceLocation modelName = ((ModelEntitlement) entitlement).getModelKey();
            if (modelName == null)
                return;

            GeometryModel model = GeometryModelManager.getModel(modelName);
            if (model == GeometryModel.EMPTY)
                return;

            GeometryModelRenderer.copyModelAngles(this.getParentModel(), model);
            GeometryModelRenderer.render(model, entitlement.getRegistryName(), matrixStack, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        });
    }
}
