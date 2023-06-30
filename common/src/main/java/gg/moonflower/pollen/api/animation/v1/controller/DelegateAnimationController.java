package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.pinwheel.api.animation.AnimationVariableStorage;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pollen.api.animation.v1.RenderAnimationTimer;
import gg.moonflower.pollen.api.animation.v1.controller.PollenAnimationController;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A simple animation controller that passes everything through a delegate controller.
 * This allows specific parts of the controller behavior to be overridden.
 *
 * @since 2.0.0
 */
public abstract class DelegateAnimationController implements PollenAnimationController {

    protected final PollenAnimationController delegate;

    public DelegateAnimationController(PollenAnimationController delegate) {
        this.delegate = delegate;
    }

    @Override
    public void tick() {
        this.delegate.tick();
    }

    @Override
    public void setLifetime(float lifetime) {
        this.delegate.setLifetime(lifetime);
    }

    @Override
    public void setRenderParameters(float xRotation, float yRotation, float limbSwing, float limbSwingAmount) {
        this.delegate.setRenderParameters(xRotation, yRotation, limbSwing, limbSwingAmount);
    }

    @Override
    public void setRenderTimer(ResourceLocation animation, @Nullable RenderAnimationTimer timer) {
        this.delegate.setRenderTimer(animation, timer);
    }

    @Override
    public RenderAnimationTimer getRenderTimer(ResourceLocation animation) {
        return this.delegate.getRenderTimer(animation);
    }

    @Override
    public MolangEnvironment getEnvironment() {
        return this.delegate.getEnvironment();
    }

    @Override
    public AnimationVariableStorage getVariables() {
        return this.delegate.getVariables();
    }

    @Override
    public Collection<PlayingAnimation> getPlayingAnimations() {
        return this.delegate.getPlayingAnimations();
    }

    @Override
    public boolean isNoAnimationPlaying() {
        return this.delegate.isNoAnimationPlaying();
    }

    @Override
    public void setAnimationTime(float time) {
        this.delegate.setAnimationTime(time);
    }

    @Override
    public void setWeight(float weight) {
        this.delegate.setWeight(weight);
    }
}
