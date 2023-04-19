package gg.moonflower.pollen.impl.render.geometry;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import gg.moonflower.pinwheel.api.transform.MatrixStack;
import gg.moonflower.pollen.api.render.wrapper.v0.JomlWrapper;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;

@ApiStatus.Internal
public class PoseStackWrapper implements MatrixStack {

    private final PoseStack poseStack;
    private final Vector3f axis;

    public PoseStackWrapper(PoseStack poseStack) {
        this.poseStack = poseStack;
        this.axis = new Vector3f();
    }

    @Override
    public void reset() {
        while (!this.poseStack.clear()) {
            this.poseStack.popPose();
        }
        this.poseStack.setIdentity();
    }

    @Override
    public void translate(float x, float y, float z) {
        this.poseStack.translate(x, y, z);
    }

    @Override
    public void rotate(Quaternionfc rotation) {
        this.poseStack.mulPose(new Quaternion(rotation.x(), rotation.y(), rotation.z(), rotation.w()));
    }

    @Override
    public void rotate(float amount, float x, float y, float z) {
        this.axis.set(x, y, z);
        this.poseStack.mulPose(new Quaternion(this.axis, amount, false));
    }

    @Override
    public void rotateXYZ(float x, float y, float z) {
        this.poseStack.mulPose(Vector3f.XP.rotation(x));
        this.poseStack.mulPose(Vector3f.YP.rotation(y));
        this.poseStack.mulPose(Vector3f.ZP.rotation(z));
    }

    @Override
    public void rotateZYX(float z, float y, float x) {
        this.poseStack.mulPose(Vector3f.ZP.rotation(z));
        this.poseStack.mulPose(Vector3f.YP.rotation(y));
        this.poseStack.mulPose(Vector3f.XP.rotation(x));
    }

    @Override
    public void scale(float x, float y, float z) {
        this.poseStack.scale(x, y, z);
    }

    @Override
    public void pushMatrix() {
        this.poseStack.pushPose();
    }

    @Override
    public void popMatrix() {
        this.poseStack.popPose();
    }

    @Override
    public Matrix4f position() {
        return JomlWrapper.set(new Matrix4f(), this.poseStack.last().pose());
    }

    @Override
    public Matrix3f normal() {
        return JomlWrapper.set(new Matrix3f(), this.poseStack.last().normal());
    }
}
