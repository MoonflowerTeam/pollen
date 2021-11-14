package gg.moonflower.pollen.core.extensions;

import net.minecraft.server.packs.resources.PreparableReloadListener;

public interface InjectableResourceManager {

    void pollen_registerReloadListenerFirst(PreparableReloadListener preparableReloadListener);
}
