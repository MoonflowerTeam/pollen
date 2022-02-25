package gg.moonflower.pollen.core.mixin.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRenderer;
import gg.moonflower.pollen.pinwheel.api.client.render.BlockRendererRegistry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockState.class)
public abstract class BlockStateMixin extends BlockBehaviour.BlockStateBase {

    private BlockStateMixin(Block block, ImmutableMap<Property<?>, Comparable<?>> immutableMap, MapCodec<BlockState> mapCodec) {
        super(block, immutableMap, mapCodec);
    }

    @Override
    public RenderShape getRenderShape() {
        BlockRenderer renderer = BlockRendererRegistry.getFirst(this.getBlock());
        if (renderer != null && renderer.getRenderShape((BlockState) (Object) this) == RenderShape.INVISIBLE)
            return RenderShape.INVISIBLE;
        return super.getRenderShape();
    }
}
