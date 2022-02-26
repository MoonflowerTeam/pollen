package gg.moonflower.pollen.core.mixin.forge.loot;

import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {

    @ModifyVariable(method = "loadLootTable", at = @At(value = "INVOKE", target = "Ljava/util/Deque;push(Ljava/lang/Object;)V", shift = At.Shift.AFTER, remap = false), remap = false, argsOnly = true)
    private static boolean modifyCustomBefore(boolean value) {
        return false;
    }
}
