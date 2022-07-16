package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.event.EventResult;
import gg.moonflower.pollen.api.event.events.entity.living.PotionEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Spider.class)
public class SpiderMixin extends Monster {

    private SpiderMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "canBeAffected", at = @At("HEAD"), cancellable = true)
    public void canBeAffected(MobEffectInstance effectInstance, CallbackInfoReturnable<Boolean> cir) {
        EventResult result = PotionEvents.APPLICABLE.invoker().applicable((LivingEntity) (Object) this, effectInstance);
        cir.setReturnValue(effectInstance.getEffect() == MobEffects.POISON ? result == EventResult.ALLOW : super.canBeAffected(effectInstance));
    }
}
