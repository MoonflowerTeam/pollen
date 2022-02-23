package gg.moonflower.pollen.pinwheel.core.client.geometry.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.RenderType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class GeometryRenderTypesImpl {

    private static final boolean IRIS_LOADED = Platform.isModLoaded("iris");

    public static RenderType wrap(RenderType renderType) {
        return IRIS_LOADED ? GeometryRenderTypesIris.wrap(renderType) : renderType;
    }
}
