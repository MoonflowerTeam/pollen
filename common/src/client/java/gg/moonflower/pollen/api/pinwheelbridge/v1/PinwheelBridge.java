package gg.moonflower.pollen.api.pinwheelbridge.v1;

import com.mojang.blaze3d.vertex.PoseStack;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import gg.moonflower.pollen.impl.render.geometry.PoseStackWrapper;

public interface PinwheelBridge {

    static MatrixStack wrap(PoseStack poseStack) {
        return new PoseStackWrapper(poseStack);
    }
}
