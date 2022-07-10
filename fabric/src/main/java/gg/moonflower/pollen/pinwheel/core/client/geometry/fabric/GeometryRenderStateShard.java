package gg.moonflower.pollen.pinwheel.core.client.geometry.fabric;

import net.coderbot.iris.layer.GbufferPrograms;
import net.minecraft.client.renderer.RenderStateShard;

public final class GeometryRenderStateShard extends RenderStateShard {

    public static final GeometryRenderStateShard INSTANCE = new GeometryRenderStateShard();

    private GeometryRenderStateShard() {
        super("iris:is_entity", GbufferPrograms::beginEntities, GbufferPrograms::endEntities);
    }
}
