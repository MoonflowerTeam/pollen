package gg.moonflower.pollen.common.events.context;

import gg.moonflower.pollen.api.event.events.entity.living.LivingEntityEvents;

public class HealContextImpl implements LivingEntityEvents.Heal.HealContext {

    private float amount;

    public HealContextImpl(float amount) {
        this.setAmount(amount);
    }

    @Override
    public float getAmount() {
        return amount;
    }

    @Override
    public void setAmount(float amount) {
        this.amount = amount;
    }
}
