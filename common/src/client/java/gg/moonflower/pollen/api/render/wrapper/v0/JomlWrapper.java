package gg.moonflower.pollen.api.render.wrapper.v0;

import com.mojang.math.Quaternion;
import gg.moonflower.pollen.client.mixin.Matrix3fAccessor;
import gg.moonflower.pollen.client.mixin.Matrix4fAccessor;
import org.joml.*;

/**
 * Bridges the vanilla Minecraft math types with JOML. In Minecraft 1.19.4 this is no longer necessary.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface JomlWrapper {

    static Quaternion set(Quaternion minecraftQuaternion, Quaternionfc jomlQuaternion) {
        minecraftQuaternion.set(jomlQuaternion.x(), jomlQuaternion.y(), jomlQuaternion.z(), jomlQuaternion.w());
        return minecraftQuaternion;
    }

    static Quaternionf set(Quaternionf jomlQuaternion, Quaternion minecraftQuaternion) {
        return jomlQuaternion.set(minecraftQuaternion.i(), minecraftQuaternion.j(), minecraftQuaternion.k(), minecraftQuaternion.r());
    }

    static com.mojang.math.Matrix4f set(com.mojang.math.Matrix4f minecraftMatrix, Matrix4fc jomlMatrix) {
        Matrix4fAccessor accessor = (Matrix4fAccessor) (Object) minecraftMatrix;
        accessor.m00(jomlMatrix.m00());
        accessor.m01(jomlMatrix.m01());
        accessor.m02(jomlMatrix.m02());
        accessor.m03(jomlMatrix.m03());

        accessor.m10(jomlMatrix.m10());
        accessor.m11(jomlMatrix.m11());
        accessor.m12(jomlMatrix.m12());
        accessor.m13(jomlMatrix.m13());

        accessor.m20(jomlMatrix.m20());
        accessor.m21(jomlMatrix.m21());
        accessor.m22(jomlMatrix.m22());
        accessor.m23(jomlMatrix.m23());

        accessor.m30(jomlMatrix.m30());
        accessor.m31(jomlMatrix.m31());
        accessor.m32(jomlMatrix.m32());
        accessor.m33(jomlMatrix.m33());
        return minecraftMatrix;
    }

    static Matrix4f set(Matrix4f jomlMatrix, com.mojang.math.Matrix4f minecraftMatrix) {
        Matrix4fAccessor accessor = (Matrix4fAccessor) (Object) minecraftMatrix;
        jomlMatrix.m00(accessor.m00());
        jomlMatrix.m01(accessor.m01());
        jomlMatrix.m02(accessor.m02());
        jomlMatrix.m03(accessor.m03());

        jomlMatrix.m10(accessor.m10());
        jomlMatrix.m11(accessor.m11());
        jomlMatrix.m12(accessor.m12());
        jomlMatrix.m13(accessor.m13());

        jomlMatrix.m20(accessor.m20());
        jomlMatrix.m21(accessor.m21());
        jomlMatrix.m22(accessor.m22());
        jomlMatrix.m23(accessor.m23());

        jomlMatrix.m30(accessor.m30());
        jomlMatrix.m31(accessor.m31());
        jomlMatrix.m32(accessor.m32());
        jomlMatrix.m33(accessor.m33());
        return jomlMatrix;
    }

    static com.mojang.math.Matrix3f set(com.mojang.math.Matrix3f minecraftMatrix, Matrix3fc jomlMatrix) {
        Matrix3fAccessor accessor = (Matrix3fAccessor) (Object) minecraftMatrix;
        accessor.m00(jomlMatrix.m00());
        accessor.m01(jomlMatrix.m01());
        accessor.m02(jomlMatrix.m02());

        accessor.m10(jomlMatrix.m10());
        accessor.m11(jomlMatrix.m11());
        accessor.m12(jomlMatrix.m12());

        accessor.m20(jomlMatrix.m20());
        accessor.m21(jomlMatrix.m21());
        accessor.m22(jomlMatrix.m22());
        return minecraftMatrix;
    }

    static Matrix3f set(Matrix3f jomlMatrix, com.mojang.math.Matrix3f minecraftMatrix) {
        Matrix3fAccessor accessor = (Matrix3fAccessor) (Object) minecraftMatrix;
        jomlMatrix.m00(accessor.m00());
        jomlMatrix.m01(accessor.m01());
        jomlMatrix.m02(accessor.m02());

        jomlMatrix.m10(accessor.m10());
        jomlMatrix.m11(accessor.m11());
        jomlMatrix.m12(accessor.m12());

        jomlMatrix.m20(accessor.m20());
        jomlMatrix.m21(accessor.m21());
        jomlMatrix.m22(accessor.m22());
        return jomlMatrix;
    }

    static com.mojang.math.Vector3d set(com.mojang.math.Vector3d minecraftVector, Vector3dc jomlVector) {
        minecraftVector.set(jomlVector.x(), jomlVector.y(), jomlVector.z());
        return minecraftVector;
    }

    static Vector3d set(Vector3d jomlVector, com.mojang.math.Vector3d minecraftVector) {
        return jomlVector.set(minecraftVector.x, minecraftVector.y, minecraftVector.z);
    }

    static com.mojang.math.Vector3f set(com.mojang.math.Vector3f minecraftVector, Vector3fc jomlVector) {
        minecraftVector.set(jomlVector.x(), jomlVector.y(), jomlVector.z());
        return minecraftVector;
    }

    static Vector3f set(Vector3f jomlVector, com.mojang.math.Vector3f minecraftVector) {
        return jomlVector.set(minecraftVector.x(), minecraftVector.y(), minecraftVector.z());
    }

    static com.mojang.math.Vector4f set(com.mojang.math.Vector4f minecraftVector, Vector4fc jomlVector) {
        minecraftVector.set(jomlVector.x(), jomlVector.y(), jomlVector.z(), jomlVector.w());
        return minecraftVector;
    }

    static Vector4f set(Vector4f jomlVector, com.mojang.math.Vector3f minecraftVector) {
        return jomlVector.set(minecraftVector.x(), minecraftVector.y(), minecraftVector.z());
    }

    static Vector4f set(Vector4f jomlVector, com.mojang.math.Vector4f minecraftVector) {
        return jomlVector.set(minecraftVector.x(), minecraftVector.y(), minecraftVector.z(), minecraftVector.w());
    }
}
