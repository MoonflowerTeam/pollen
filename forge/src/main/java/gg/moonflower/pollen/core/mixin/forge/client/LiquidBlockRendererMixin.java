package gg.moonflower.pollen.core.mixin.forge.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

@Mixin(LiquidBlockRenderer.class)
public class LiquidBlockRendererMixin {

    @Unique
    private final Map<Fluid, TextureAtlasSprite[]> customFluidSprites = new HashMap<>();
    @Unique
    private final Map<FluidState, BlockState> fluidStateCache = new WeakHashMap<>();

    @Unique
    private BlockAndTintGetter captureLevel;
    @Unique
    private FluidState captureFluid;
    @Unique
    private BlockPos capturePos;

    @Inject(method = "setupSprites", at = @At("TAIL"))
    public void setupSprites(CallbackInfo ci) {
        this.customFluidSprites.clear();
        this.fluidStateCache.clear();
        for (Fluid fluid : Registry.FLUID) {
            if (!(fluid instanceof PollinatedFluid pollinatedFluid))
                continue;
            Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
            this.customFluidSprites.put(fluid, new TextureAtlasSprite[]{atlas.apply(pollinatedFluid.getStillTextureName()), atlas.apply(pollinatedFluid.getFlowingTextureName()), pollinatedFluid.getOverlayTextureName() != null ? atlas.apply(pollinatedFluid.getOverlayTextureName()) : null});
        }
    }

    @Inject(method = "tesselate", at = @At("HEAD"))
    public void captureFluid(BlockAndTintGetter lightReader, BlockPos pos, VertexConsumer vertexBuilder, BlockState state, FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        this.captureLevel = lightReader;
        this.captureFluid = fluidState;
        this.capturePos = pos;
    }

    @ModifyVariable(method = "tesselate", at = @At(value = "STORE"), ordinal = 0)
    public boolean modifyIsLava(boolean value) {
        return value || this.customFluidSprites.containsKey(this.captureFluid.getType());
    }

    @ModifyVariable(method = "tesselate", at = @At(value = "STORE"), ordinal = 0)
    public TextureAtlasSprite[] modifySprites(TextureAtlasSprite[] value) {
        return this.customFluidSprites.getOrDefault(this.captureFluid.getType(), value);
    }

    @ModifyVariable(method = "tesselate", at = @At(value = "STORE"), ordinal = 0)
    public int modifyColor(int value) {
        // Fix custom fluids not respecting normal block colors
        return this.customFluidSprites.containsKey(this.captureFluid.getType()) ? Minecraft.getInstance().getBlockColors().getColor(this.fluidStateCache.computeIfAbsent(this.captureFluid, FluidState::createLegacyBlock), this.captureLevel, this.capturePos, 0) : value;
    }
}
