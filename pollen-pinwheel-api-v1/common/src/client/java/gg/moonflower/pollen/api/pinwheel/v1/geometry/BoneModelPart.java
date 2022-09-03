package gg.moonflower.pollen.api.pinwheel.v1.geometry;

import gg.moonflower.pollen.api.pinwheel.v1.animation.AnimatedModelPart;

/**
 * A model part that uses {@link GeometryModelData.Bone} as the source of geometry.
 *
 * @author Ocelot
 * @since 2.0.0
 */
public interface BoneModelPart extends AnimatedModelPart {

    /**
     * Resets the transformation of this part.
     *
     * @param resetChildren Whether to reset the transformations of all child parts
     */
    void resetTransform(boolean resetChildren);

    /**
     * @return The bone this model renderer is rendering
     */
    GeometryModelData.Bone getBone();
}
