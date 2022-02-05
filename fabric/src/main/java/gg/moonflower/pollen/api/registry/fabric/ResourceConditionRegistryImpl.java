package gg.moonflower.pollen.api.registry.fabric;

import gg.moonflower.pollen.api.resource.condition.PollinatedResourceCondition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ResourceConditionRegistryImpl {

    public static void register(ResourceLocation name, PollinatedResourceCondition condition) {
        throw new UnsupportedOperationException("Fabric does not have recipe conditions yet");
    }
}
