package gg.moonflower.pollen.pinwheel.core.client.geometry.fabric;

import net.coderbot.iris.layer.OuterWrappedRenderType;
import net.coderbot.iris.mixin.rendertype.RenderStateShardAccessor;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GeometryRenderTypesIris {

    static RenderType wrap(RenderType renderType) {
        return new OuterWrappedRenderType("iris:" + ((RenderStateShardAccessor) renderType).getName(), renderType, GeometryRenderStateShard.INSTANCE);
    }
}
