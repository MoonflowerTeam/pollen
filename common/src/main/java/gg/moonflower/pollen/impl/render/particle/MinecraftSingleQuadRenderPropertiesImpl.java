package gg.moonflower.pollen.impl.render.particle;

import gg.moonflower.pinwheel.impl.particle.render.SingleQuadRenderPropertiesImpl;
import gg.moonflower.pollen.api.render.particle.v1.MinecraftSingleQuadRenderProperties;
import net.minecraft.client.renderer.LightTexture;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class MinecraftSingleQuadRenderPropertiesImpl extends SingleQuadRenderPropertiesImpl implements MinecraftSingleQuadRenderProperties {

    private int packedLight;

    public MinecraftSingleQuadRenderPropertiesImpl() {
        this.packedLight = LightTexture.FULL_BRIGHT;
    }

    @Override
    public int getPackedLight() {
        return this.packedLight;
    }

    @Override
    public void setPackedLight(int packedLight) {
        this.packedLight = packedLight;
    }
}
