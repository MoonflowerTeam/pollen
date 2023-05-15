package gg.moonflower.pollen.api.animation.v1.controller;

import gg.moonflower.pinwheel.api.animation.AnimationController;
import net.minecraft.network.FriendlyByteBuf;

public interface SerializableAnimationController extends AnimationController {

    void writeToNetwork(FriendlyByteBuf buf);

    void readFromNetwork(FriendlyByteBuf buf);

    boolean isDirty();

    void setDirty(boolean dirty);
}
