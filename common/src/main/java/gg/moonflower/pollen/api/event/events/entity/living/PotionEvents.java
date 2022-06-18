package gg.moonflower.pollen.api.event.events.entity.living;

import gg.moonflower.pollen.api.event.PollinatedEvent;
import gg.moonflower.pollen.api.registry.EventRegistry;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class PotionEvents {

    public static final PollinatedEvent<Applicable> APPLICABLE = EventRegistry.createResult(Applicable.class);

    private PotionEvents() {
    }

    /**
     * Fired to check if an effect can be applied to an entity.
     *
     * @author ebo2022
     * @since 2.0.0
     */
    @FunctionalInterface
    public interface Applicable {

        /**
         * Called when checking when the specified effect is applicable to the given entity.
         *
         * @param entity         The entity that would receive the effect
         * @param effectInstance The effect that is being checked to be applied
         * @return The result for this event. {@link InteractionResult#PASS} will continue onto the next iteration and use vanilla logic, while any others will override vanilla behavior
         */
        InteractionResult applicable(LivingEntity entity, MobEffectInstance effectInstance);
    }

    public interface Add {

    }

    public interface Remove {

    }

    public interface Expire {

    }
}
