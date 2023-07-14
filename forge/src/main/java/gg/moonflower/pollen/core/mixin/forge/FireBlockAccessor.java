package gg.moonflower.pollen.core.mixin.forge;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FireBlock.class)
public interface FireBlockAccessor {

    @Invoker
    void invokeSetFlammable(Block block, int encouragement, int flammability);
}
