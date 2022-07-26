package gg.moonflower.pollen.api.registry.client.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pollen.api.client.render.DynamicItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@ApiStatus.Internal
public class ItemRendererRegistryImpl {

    private static final Field RENDER_PROPERTIES_FIELD;

    static {
        try {
            RENDER_PROPERTIES_FIELD = Item.class.getDeclaredField("renderProperties");
            RENDER_PROPERTIES_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get renderProperties from Item.class", e);
        }
    }

    public static void registerRenderer(ItemLike item, DynamicItemRenderer renderer) {
        try {
            RENDER_PROPERTIES_FIELD.set(item.asItem(), new RenderPropertiesWrapper((IClientItemExtensions) item.asItem().getRenderPropertiesInternal(), renderer));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to set renderProperties renderer", e);
        }
    }

    private static class ForgeWrapper extends BlockEntityWithoutLevelRenderer {

        private final DynamicItemRenderer renderer;

        private ForgeWrapper(DynamicItemRenderer renderer) {
            super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
            this.renderer = renderer;
        }

        @Override
        public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource multiBufferSource, int packedLight, int combinedOverlay) {
            this.renderer.render(stack, transformType, matrixStack, multiBufferSource, packedLight, combinedOverlay);
        }
    }

    private static class RenderPropertiesWrapper implements IClientItemExtensions {

        private final IClientItemExtensions parent;
        private final BlockEntityWithoutLevelRenderer renderer;

        private RenderPropertiesWrapper(@Nullable IClientItemExtensions parent, DynamicItemRenderer renderer) {
            this.parent = parent != null ? parent : IClientItemExtensions.DEFAULT;
            this.renderer = new ForgeWrapper(renderer);
        }

        @Override
        public Font getFont(ItemStack stack, FontContext context) {
            return this.parent.getFont(stack, context);
        }

        @Override
        public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
            return this.parent.getHumanoidArmorModel(entityLiving, itemStack, armorSlot, _default);
        }

        @Override
        public void renderHelmetOverlay(ItemStack stack, Player player, int width, int height, float partialTicks) {
            this.parent.renderHelmetOverlay(stack, player, width, height, partialTicks);
        }

        @Override
        public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return this.renderer;
        }
    }
}
