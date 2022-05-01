package gg.moonflower.pollen.api.registry.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.Collection;
import java.util.Collections;

// TODO Move package in 2.0.0

/**
 * @author Ocelot
 * @since 1.0.0
 */
public interface PollinatedPreparableReloadListener extends PreparableReloadListener {

    /**
     * @return The ID of this listener. This is to allow listener dependencies
     */
    ResourceLocation getPollenId();

    /**
     * @return All dependencies of this listener. {@link PollinatedPreparableReloadListener#getPollenId()} is used for id checking
     */
    default Collection<ResourceLocation> getPollenDependencies() {
        return Collections.emptyList();
    }
}
