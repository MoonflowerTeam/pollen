package gg.moonflower.pollen.api.registry.client.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.api.client.render.DynamicItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.util.function.Supplier;

@ApiStatus.Internal
public class ItemRendererRegistryImpl {

    private static final Field ITEM_FIELD;

    static {
        try {
            ITEM_FIELD = Item.class.getDeclaredField("ister");
            ITEM_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get ister from Item.class", e);
        }
    }

    public static void registerRenderer(ItemLike item, DynamicItemRenderer renderer) {
        try {
            ITEM_FIELD.set(item.asItem(), (Supplier<BlockEntityWithoutLevelRenderer>) () -> new ForgeWrapper(renderer));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set item renderer", e);
        }
    }

    private static class ForgeWrapper extends BlockEntityWithoutLevelRenderer {

        private final DynamicItemRenderer renderer;

        private ForgeWrapper(DynamicItemRenderer renderer) {
            this.renderer = renderer;
        }

        @Override
        public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight, int combinedOverlay) {
            this.renderer.render(stack, transformType, matrixStack, multiBufferSource, packedLight, combinedOverlay);
        }
    }
}
