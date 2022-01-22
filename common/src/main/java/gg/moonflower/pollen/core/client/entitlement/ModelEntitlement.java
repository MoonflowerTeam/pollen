package gg.moonflower.pollen.core.client.entitlement;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface ModelEntitlement {

    @Nullable
    ResourceLocation getModelKey();

    @Nullable
    String getModelUrl();
}
