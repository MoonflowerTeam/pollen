package gg.moonflower.pollen.api.registry.resource.fabric;

import gg.moonflower.pollen.api.registry.resource.PollinatedPreparableReloadListener;
import gg.moonflower.pollen.api.util.PollinatedModContainer;
import gg.moonflower.pollen.api.util.fabric.PollinatedModContainerImpl;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ResourceRegistryImpl {

    public static void registerReloadListener(PackType type, PollinatedPreparableReloadListener listener) {
        ResourceManagerHelper.get(type).registerReloadListener(new PollinatedPreparableReloadListenerWrapper(listener));
    }

    public static boolean registerBuiltinResourcePack(ResourceLocation id, PollinatedModContainer container, boolean enabledByDefault) {
        return ResourceManagerHelper.registerBuiltinResourcePack(id, ((PollinatedModContainerImpl) container).getFabricContainer(), enabledByDefault ? ResourcePackActivationType.DEFAULT_ENABLED : ResourcePackActivationType.NORMAL);
    }
}
