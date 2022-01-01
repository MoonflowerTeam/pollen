package gg.moonflower.pollen.core.mixin.client;

import net.minecraft.client.model.geom.ModelPart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ModelPart.Polygon.class)
public interface ModelPartPolygonAccessor {

    @Accessor
    ModelPart.Vertex[] getVertices();
}
