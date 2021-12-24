package gg.moonflower.pollen.api.registry.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Collection;
import java.util.Collections;

public interface PollinatedPreparableReloadListener extends PreparableReloadListener {

    ResourceLocation getPollenId();

    default Collection<ResourceLocation> getPollenDependencies() {
        return Collections.emptyList();
    }
}
