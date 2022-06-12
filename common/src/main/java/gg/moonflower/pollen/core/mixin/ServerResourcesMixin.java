package gg.moonflower.pollen.core.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import gg.moonflower.pollen.api.resource.modifier.ResourceModifierManager;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = ReloadableServerResources.class, priority = 900)
public class ServerResourcesMixin {

    @ModifyReturnValue(method = "listeners", at = @At("RETURN"))
    public List<PreparableReloadListener> addListeners(List<PreparableReloadListener> listeners) {
        List<PreparableReloadListener> list = new ArrayList<>(listeners);
        list.add(0, ResourceModifierManager.createServerReloader((ReloadableServerResources) (Object) this));
        return list;
    }
}
