package gg.moonflower.pollen.common.events.context;

import gg.moonflower.pollen.api.event.events.entity.living.LivingEntityEvents;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class LivingDamageContextImpl implements LivingEntityEvents.LivingDamageEvent.Context {
    private float damageAmount;

    public LivingDamageContextImpl(float damageAmount) {
        this.damageAmount = damageAmount;
    }

    @Override
    public float getDamageAmount() {
        return damageAmount;
    }

    @Override
    public void setDamageAmount(float amount) {
        this.damageAmount = amount;
    }
}
