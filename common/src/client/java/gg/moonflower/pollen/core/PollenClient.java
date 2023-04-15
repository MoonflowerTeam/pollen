package gg.moonflower.pollen.core;

import gg.moonflower.pollen.impl.render.geometry.GeometryModelManagerImpl;
import gg.moonflower.pollen.impl.render.geometry.texture.GeometryTextureManagerImpl;
import gg.moonflower.pollen.impl.render.shader.PollenShaderTypes;

public class PollenClient {

    public static void init() {
        GeometryModelManagerImpl.init();
        GeometryTextureManagerImpl.init();
        PollenShaderTypes.init();
    }

    public static void postInit() {}
}
