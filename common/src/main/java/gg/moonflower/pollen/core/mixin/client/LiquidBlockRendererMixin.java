package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {

    @Unique
    private final Map<Fluid, TextureAtlasSprite[]> customFluidSprites = new HashMap<>();

    @Inject(method = "setupSprites", at = @At("TAIL"))
    public void setupSprites(CallbackInfo ci) {
        this.customFluidSprites.clear();
        for (Fluid fluid : Registry.FLUID) {
            if (!(fluid instanceof PollinatedFluid))
                return;
            PollinatedFluid pollinatedFluid = (PollinatedFluid) fluid;
            Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
            this.customFluidSprites.put(fluid, new TextureAtlasSprite[]{atlas.apply(pollinatedFluid.getStillTextureName()), atlas.apply(pollinatedFluid.getFlowingTextureName())});
        }
    }
}
