package gg.moonflower.pollen.core.mixin;

import gg.moonflower.pollen.core.extensions.InjectableResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(value = ReloadableResourceManager.class)
public class SimpleReloadableResourceManagerMixin implements InjectableResourceManager {

    @Shadow
    @Final
    private List<PreparableReloadListener> listeners;

    @Override
    public void pollen_registerReloadListenerFirst(PreparableReloadListener preparableReloadListener) {
        this.listeners.add(0, preparableReloadListener);
    }
}
