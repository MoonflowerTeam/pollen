package gg.moonflower.pollen.api.pinwheel.v1.animation;

import com.mojang.math.Vector3f;
import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryModelData;

/**
 * A model part with an animation position.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public interface AnimatedModelPart {

    /**
     * @return The pose of this bone for animation
     */
    AnimationPose getAnimationPose();

    /**
     * @return All locators in this bone
     */
    GeometryModelData.Locator[] getLocators();

    /**
     * A position, rotation, and scale transformation applied on top of default positions for animations.
     *
     * @author Ocelot
     * @since 1.0.0
     */
    record AnimationPose(Vector3f position, Vector3f rotation, Vector3f scale) {

        public AnimationPose() {
            this(new Vector3f(), new Vector3f(), new Vector3f(1, 1, 1));
        }

        /**
         * Resets the transformation for this pose.
         */
        public void reset() {
            this.position.set(0, 0, 0);
            this.rotation.set(0, 0, 0);
            this.scale.set(1, 1, 1);
        }

        /**
         * @return Whether this pose is set to "identity"
         */
        public boolean isIdentity() {
            return this.position.hashCode() == 0 && this.rotation.hashCode() == 0 && this.scale.hashCode() == 1333788672; // 1333788672 is the hash code of a 1, 1, 1 vector;
        }

        /**
         * Applies additional transformations that can be dynamically changed.
         *
         * @param x         The x offset
         * @param y         The y offset
         * @param z         The z offset
         * @param rotationX The x rotation offset
         * @param rotationY The y rotation offset
         * @param rotationZ The z rotation offset
         * @param scaleX    The x factor
         * @param scaleY    The y factor
         * @param scaleZ    The z factor
         */
        public void add(float x, float y, float z, float rotationX, float rotationY, float rotationZ, float scaleX, float scaleY, float scaleZ) {
            this.position.add(x, y, z);
            this.rotation.add(rotationX, rotationY, rotationZ);
            this.scale.mul(scaleX, scaleY, scaleZ);
        }
    }
}
