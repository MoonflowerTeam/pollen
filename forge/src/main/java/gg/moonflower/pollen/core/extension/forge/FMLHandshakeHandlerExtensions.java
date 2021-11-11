package gg.moonflower.pollen.core.extension.forge;

import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.Future;

@ApiStatus.Internal
public interface FMLHandshakeHandlerExtensions {

    void pollen_addWait(Future<?> wait);
}
