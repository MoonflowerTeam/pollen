package gg.moonflower.pollen.api.registry.resource;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.advancements.CriterionTrigger;

/**
 * Registers criterion triggers for advancements.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public final class CriterionRegistry {

    private CriterionRegistry() {
    }

    /**
     * Registers a new criterion for a trigger for advancements.
     *
     * @param criterion the criterion to registered
     * @param <T>       the type of the criterion
     * @return The registered criterion
     * @throws IllegalArgumentException if a criterion with the same {@link CriterionTrigger#getId() id} exists
     */
    @ExpectPlatform
    public static <T extends CriterionTrigger<?>> T register(T criterion) {
        return Platform.error();
    }
}
