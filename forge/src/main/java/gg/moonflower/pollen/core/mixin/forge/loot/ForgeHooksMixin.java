package gg.moonflower.pollen.core.mixin.forge.loot;

import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {

    @ModifyVariable(method = "loadLootTable", at = @At("HEAD"), ordinal = 0, remap = false, argsOnly = true)
    public boolean modifyCustom(boolean value) {
        return false;
    }
}
