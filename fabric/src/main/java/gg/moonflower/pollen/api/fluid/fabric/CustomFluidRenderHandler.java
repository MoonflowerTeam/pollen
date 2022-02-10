package gg.moonflower.pollen.api.fluid.fabric;

import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

@ApiStatus.Internal
public class CustomFluidRenderHandler implements FluidRenderHandler {

    private final Map<FluidState, BlockState> fluidStateCache = new WeakHashMap<>();

    private final PollinatedFluid fluid;
    private final TextureAtlasSprite[] sprites;

    public CustomFluidRenderHandler(PollinatedFluid fluid) {
        this.fluid = fluid;
        this.sprites = new TextureAtlasSprite[2];
    }

    @Override
    public TextureAtlasSprite[] getFluidSprites(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
        return sprites;
    }

    @Override
    public void reloadTextures(TextureAtlas textureAtlas) {
        this.fluidStateCache.clear();
        this.sprites[0] = textureAtlas.getSprite(this.fluid.getStillTextureName());
        this.sprites[1] = textureAtlas.getSprite(this.fluid.getFlowingTextureName());

        // TODO add overlay texture
//        if (overlayTexture != null) {
//            sprites[2] = textureAtlas.getSprite(overlayTexture);
//        }
    }

    @Override
    public int getFluidColor(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos, FluidState state) {
        return Minecraft.getInstance().getBlockColors().getColor(this.fluidStateCache.computeIfAbsent(state, FluidState::createLegacyBlock), level, pos, 0);
    }
}
