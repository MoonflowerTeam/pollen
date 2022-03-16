package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public class ServerResourcesMixin {

    @Inject(method = "listeners", at = @At("TAIL"), cancellable = true)
    public void addListeners(CallbackInfoReturnable<List<PreparableReloadListener>> cir) {
        List<PreparableReloadListener> list = new ArrayList<>(cir.getReturnValue());
        list.add(0, ResourceModifierManager.createServerReloader((ReloadableServerResources) (Object) this));
        cir.setReturnValue(list);
    }
}
