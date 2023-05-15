package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pinwheel.api.animation.AnimationController;

public interface PollenAnimationController extends AnimationController {

    void setLifetime(float lifetime);

    void setRenderParameters(float xRotation, float yRotation, float limbSwing, float limbSwingAmount);
}
