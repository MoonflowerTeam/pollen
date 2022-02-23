package gg.moonflower.pollen.pinwheel.core.client.geometry.fabric;

import net.coderbot.iris.layer.GbufferProgram;
import net.coderbot.iris.layer.IrisRenderTypeWrapper;
import net.coderbot.iris.layer.UseProgramRenderStateShard;
import net.coderbot.iris.mixin.rendertype.RenderStateShardAccessor;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GeometryRenderTypesIris {

    static RenderType wrap(RenderType renderType) {
        return new IrisRenderTypeWrapper("iris:" + ((RenderStateShardAccessor) renderType).getName(), renderType, new UseProgramRenderStateShard(GbufferProgram.ENTITIES));
    }
}
