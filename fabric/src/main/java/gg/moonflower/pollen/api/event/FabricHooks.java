package gg.moonflower.pollen.api.event;

import gg.moonflower.pollen.api.event.events.entity.living.LivingEntityEvents;
import gg.moonflower.pollen.api.event.events.world.WorldEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FabricHooks {

    private FabricHooks() {
    }

    public static int processBonemeal(Level level, BlockPos pos, BlockState state, ItemStack stack) {
        ResultContext resultContext = new ResultContextImpl();
        boolean event = WorldEvents.BONEMEAL.invoker().bonemeal(level, pos, state, stack, resultContext);
        if (!event) {
            return -1;
        } else if (resultContext.getResult() == EventResult.ALLOW) {
            if (!level.isClientSide) {
                stack.shrink(1);
            }
            return 1;
        } else {
            return 0;
        }
    }

    public static class HealContextImpl implements LivingEntityEvents.Heal.HealContext {

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

    public static class LivingDamageContextImpl implements LivingEntityEvents.Damage.Context {
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

    public static class ResultContextImpl implements ResultContext {
        private EventResult result;

        public ResultContextImpl() {
            this.setResult(EventResult.DEFAULT);
        }

        @Override
        public EventResult getResult() {
            return result;
        }

        @Override
        public void setResult(EventResult result) {
            this.result = result;
        }
    }
}
