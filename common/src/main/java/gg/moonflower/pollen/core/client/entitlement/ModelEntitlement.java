package gg.moonflower.pollen.core.client.entitlement;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * An entitlement that supplies a model that must be reloaded.
 *
 * @author Ocelot
 */
@ApiStatus.Internal
public interface ModelEntitlement {

    /**
     * @return The key for the model
     */
    @Nullable
    ResourceLocation getModelKey();

    /**
     * @return The URLs to the model files
     */
    String[] getModelUrls();
}
