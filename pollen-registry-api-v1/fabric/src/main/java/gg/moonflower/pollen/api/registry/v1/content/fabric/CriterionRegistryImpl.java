package gg.moonflower.pollen.api.registry.v1.content.fabric;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CriterionRegistryImpl {

    public static <T extends CriterionTrigger<?>> T register(T criterion) {
        return CriteriaTriggers.register(criterion);
    }
}
