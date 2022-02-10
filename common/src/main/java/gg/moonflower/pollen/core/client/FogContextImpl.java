package gg.moonflower.pollen.core.client;

import com.mojang.blaze3d.systems.RenderSystem;
import gg.moonflower.pollen.api.event.events.client.render.FogEvents;
import net.minecraft.client.renderer.FogRenderer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FogContextImpl implements FogEvents.FogContext {

    @Override
    public void disableFog() {
        FogRenderer.setupNoFog();
    }

    @Override
    public void fogStart(float nearPlane) {
        RenderSystem.setShaderFogStart(nearPlane);
    }

    @Override
    public void fogEnd(float farPlane) {
        RenderSystem.setShaderFogEnd(farPlane);
    }
}
