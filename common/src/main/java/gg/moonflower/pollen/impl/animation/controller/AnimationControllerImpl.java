package gg.moonflower.pollen.impl.animation.controller;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangEnvironmentBuilder;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.AnimationVariableStorage;
import gg.moonflower.pinwheel.api.animation.PlayingAnimation;
import gg.moonflower.pollen.api.animation.v1.AnimationRuntime;
import gg.moonflower.pollen.api.animation.v1.RenderAnimationTimer;
import gg.moonflower.pollen.api.animation.v1.controller.PollenAnimationController;
import gg.moonflower.pollen.impl.animation.PollenPlayingAnimationImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
public abstract class AnimationControllerImpl implements PollenAnimationController {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AnimationController.class);
    private final MolangEnvironment environment;
    private final AnimationVariableStorage storage;
    private final AnimationVariableStorage.Value lifetime;
    private final AnimationVariableStorage.Value xRotation;
    private final AnimationVariableStorage.Value yRotation;
    private final AnimationVariableStorage.Value limbSwing;
    private final AnimationVariableStorage.Value limbSwingAmount;

    public AnimationControllerImpl(MolangRuntime runtime) {
        MolangEnvironmentBuilder<?> runtimeBuilder = runtime.edit();
        AnimationRuntime.addGlobal(runtimeBuilder);

        AnimationVariableStorage.Builder storageBuilder = AnimationVariableStorage.builder();
        this.lifetime = storageBuilder.add("life_time");
        this.xRotation = storageBuilder.add("head_x_rotation");
        this.yRotation = storageBuilder.add("head_y_rotation");
        this.limbSwing = storageBuilder.add("limb_swing");
        this.limbSwingAmount = storageBuilder.add("limb_swing_amount");
        this.storage = storageBuilder.create();

        runtimeBuilder.setVariables(this.storage);
        this.environment = runtimeBuilder.create();
    }

    @Override
    public void tick() {
        for (PlayingAnimation playingAnimation : this.getPlayingAnimations()) {
            if (playingAnimation instanceof PollenPlayingAnimationImpl impl) {
                impl.tick();
            }
        }
    }

    @Override
    public MolangEnvironment getEnvironment() {
        return this.environment;
    }

    @Override
    public AnimationVariableStorage getVariables() {
        return this.storage;
    }

    @Override
    public void setLifetime(float lifetime) {
        this.lifetime.setValue(lifetime);
    }

    @Override
    public void setRenderParameters(float xRotation, float yRotation, float limbSwing, float limbSwingAmount) {
        this.xRotation.setValue(xRotation);
        this.yRotation.setValue(yRotation);
        this.limbSwing.setValue(limbSwing);
        this.limbSwingAmount.setValue(limbSwingAmount);
    }

    @Override
    public void setRenderTimer(ResourceLocation animation, @Nullable RenderAnimationTimer timer) {
    }

    @Override
    public RenderAnimationTimer getRenderTimer(ResourceLocation animation) {
        return RenderAnimationTimer.LINEAR;
    }
}
