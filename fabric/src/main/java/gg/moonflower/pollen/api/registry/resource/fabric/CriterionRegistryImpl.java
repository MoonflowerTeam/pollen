package gg.moonflower.pollen.api.registry.resource.fabric;

import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.minecraft.advancements.CriterionTrigger;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CriterionRegistryImpl {

    public static <T extends CriterionTrigger<?>> T register(T criterion) {
        return CriterionRegistry.register(criterion);
    }
}
