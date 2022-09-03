package gg.moonflower.pollen.impl.pinwheel.geometry;

import gg.moonflower.pollen.api.pinwheel.v1.geometry.GeometryModelData;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class JavaModelConverter {

    public static GeometryModelData.Bone[] convert(ModelPart... parents) {
        throw new UnsupportedOperationException("Java model conversion is not supported on 1.16");
    }
}
