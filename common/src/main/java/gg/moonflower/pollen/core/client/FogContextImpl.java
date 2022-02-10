package gg.moonflower.pollen.core.client;

import com.mojang.blaze3d.systems.RenderSystem;
import gg.moonflower.pollen.api.event.events.client.render.FogEvents;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FogContextImpl implements FogEvents.FogContext {

    @Override
    public void enableFog() {
        RenderSystem.enableFog();
    }

    @Override
    public void disableFog() {
        RenderSystem.disableFog();
    }

    @Override
    public void fogMode(int glMode) {
        RenderSystem.fogMode(glMode);
    }

    @Override
    public void fogDensity(float density) {
        RenderSystem.fogDensity(density);
    }

    @Override
    public void fogStart(float nearPlane) {
        RenderSystem.fogStart(nearPlane);
    }

    @Override
    public void fogEnd(float farPlane) {
        RenderSystem.fogEnd(farPlane);
    }

    @Override
    public void setupNvFogDistance() {
        RenderSystem.setupNvFogDistance();
    }
}
