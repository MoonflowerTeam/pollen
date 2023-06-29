package gg.moonflower.pollen.impl.animation.controller;

import gg.moonflower.pollen.api.animation.v1.controller.DelegateAnimationController;
import gg.moonflower.pollen.api.animation.v1.controller.IdleAnimationController;
import gg.moonflower.pollen.api.animation.v1.controller.PollenAnimationController;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class IdleAnimationControllerImpl extends DelegateAnimationController implements IdleAnimationController {

    public IdleAnimationControllerImpl(PollenAnimationController delegate) {
        super(delegate);
    }

    @Override
    public void tick() {
    }

    @Override
    public ResourceLocation[] getIdleAnimations() {
        return new ResourceLocation[0];
    }

    @Override
    public void setIdleAnimations(ResourceLocation... animations) {
    }
}
