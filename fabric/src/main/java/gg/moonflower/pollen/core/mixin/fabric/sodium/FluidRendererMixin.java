package gg.moonflower.pollen.core.mixin.fabric.sodium;

import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import gg.moonflower.pollen.core.mixin.fabric.client.BlockColorsAccessor;
import me.jellysquid.mods.sodium.client.model.light.LightPipeline;
import me.jellysquid.mods.sodium.client.model.light.data.QuadLightData;
import me.jellysquid.mods.sodium.client.model.quad.ModelQuadView;
import me.jellysquid.mods.sodium.client.model.quad.blender.BiomeColorBlender;
import me.jellysquid.mods.sodium.client.render.chunk.compile.buffers.ChunkModelBuffers;
import me.jellysquid.mods.sodium.client.render.pipeline.FluidRenderer;
import me.jellysquid.mods.sodium.client.util.color.ColorABGR;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

@Mixin(FluidRenderer.class)
public class FluidRendererMixin {

    @Shadow
    @Final
    private BiomeColorBlender biomeColorBlender;
    @Shadow
    @Final
    private int[] quadColors;
    @Shadow
    @Final
    private QuadLightData quadLightData;
    @Unique
    private final Map<Fluid, TextureAtlasSprite[]> customFluidSprites = new HashMap<>();
    @Unique
    private final Map<FluidState, BlockState> fluidStateCache = new WeakHashMap<>();

    @Unique
    private FluidState captureFluid;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void setupSprites(CallbackInfo ci) {
        this.customFluidSprites.clear();
        for (Fluid fluid : Registry.FLUID) {
            if (!(fluid instanceof PollinatedFluid))
                continue;
            PollinatedFluid pollinatedFluid = (PollinatedFluid) fluid;
            Function<ResourceLocation, TextureAtlasSprite> atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
            this.customFluidSprites.put(fluid, new TextureAtlasSprite[]{atlas.apply(pollinatedFluid.getStillTextureName()), atlas.apply(pollinatedFluid.getFlowingTextureName())});
        }
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void captureFluid(BlockAndTintGetter lightReader, FluidState fluidState, BlockPos pos, ChunkModelBuffers u3, CallbackInfoReturnable<Boolean> cir) {
        this.captureFluid = fluidState;
    }

    @ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lme/jellysquid/mods/sodium/client/model/light/LightPipelineProvider;getLighter(Lme/jellysquid/mods/sodium/client/model/light/LightMode;)Lme/jellysquid/mods/sodium/client/model/light/LightPipeline;"), ordinal = 6)
    public boolean modifyIsLava(boolean value) {
        return value || this.customFluidSprites.containsKey(this.captureFluid.getType());
    }

    @ModifyVariable(method = "render", at = @At("STORE"), ordinal = 0)
    public TextureAtlasSprite[] modifySprites(TextureAtlasSprite[] value) {
        return this.customFluidSprites.getOrDefault(this.captureFluid.getType(), value);
    }

    @Inject(method = "calculateQuadColors", at = @At("TAIL"))
    public void modifyColor(ModelQuadView quad, BlockAndTintGetter world, BlockPos pos, LightPipeline lighter, Direction dir, float brightness, boolean colorized, CallbackInfo ci) {
        // Fix custom fluids not respecting normal block colors
        if (this.customFluidSprites.containsKey(this.captureFluid.getType())) {
            BlockState state = this.fluidStateCache.computeIfAbsent(this.captureFluid, FluidState::createLegacyBlock);
            BlockColor color = ((BlockColorsAccessor) Minecraft.getInstance().getBlockColors()).getBlockColors().byId(Registry.BLOCK.getId(state.getBlock()));
            if (color != null) {
                int[] biomeColors = this.biomeColorBlender.getColors(color, world, state, pos, quad);
                for (int i = 0; i < 4; ++i)
                    this.quadColors[i] = ColorABGR.mul(biomeColors != null ? biomeColors[i] : -1, this.quadLightData.br[i] * brightness);
            }
        }
    }
}
