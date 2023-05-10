package gg.moonflower.pollen.api.animation.v1.controller;

import net.minecraft.network.FriendlyByteBuf;

public interface SerializableAnimationController extends PollenAnimationController {

    void writeToNetwork(FriendlyByteBuf buf);

    void readFromNetwork(FriendlyByteBuf buf);
}
