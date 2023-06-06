package gg.moonflower.pollen.impl.mixin;

import gg.moonflower.pinwheel.api.animation.AnimationData;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pollen.impl.animation.PollenPlayingAnimationImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayingAnimation.class, remap = false)
public interface PlayingAnimationMixin {

    @Inject(method = "of", at = @At("HEAD"), cancellable = true, remap = false)
    private static void wrap(AnimationData animation, CallbackInfoReturnable<PlayingAnimation> cir) {
        cir.setReturnValue(new PollenPlayingAnimationImpl(animation));
    }
}
