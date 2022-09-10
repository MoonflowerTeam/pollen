package gg.moonflower.pollen.api.registry.v1.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.ItemStack;

/**
 * Functions as an implementation for items to render themselves using code.
 */
@FunctionalInterface
public interface DynamicItemRenderer {

    /**
     * Draws the specified item stack.
     *
     * @param stack             The stack to render
     * @param transformType     The transformation applied (hand, head, etc.)
     * @param matrixStack       The stack of matrix transformations
     * @param multiBufferSource The source of render buffers
     * @param packedLight       The packed light UV coordinates
     * @param combinedOverlay   The packed overlay UV coordinates
     */
    void render(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight, int combinedOverlay);
}
