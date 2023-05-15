package gg.moonflower.pollen.impl.animation.controller;

import gg.moonflower.pinwheel.api.animation.AnimationController;
import gg.moonflower.pinwheel.api.animation.AnimationVariableStorage;
import gg.moonflower.pollen.api.animation.v1.AnimationRuntime;
import gg.moonflower.pollen.api.animation.v1.controller.PollenAnimationController;
import io.github.ocelot.molangcompiler.api.MolangEnvironment;
import io.github.ocelot.molangcompiler.api.MolangRuntime;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

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

    public AnimationControllerImpl(MolangRuntime.Builder builder) {
        AnimationRuntime.addGlobal(builder);
        this.storage = AnimationVariableStorage.create(Set.of("life_time", "head_x_rotation", "head_y_rotation", "limb_swing", "limb_swing_amount"));
        this.lifetime = Objects.requireNonNull(this.storage.getField("life_time"));
        this.xRotation = Objects.requireNonNull(this.storage.getField("head_x_rotation"));
        this.yRotation = Objects.requireNonNull(this.storage.getField("head_y_rotation"));
        this.limbSwing = Objects.requireNonNull(this.storage.getField("limb_swing"));
        this.limbSwingAmount = Objects.requireNonNull(this.storage.getField("limb_swing_amount"));

        builder.setVariables(this.storage);
        this.environment = builder.create();
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
}
