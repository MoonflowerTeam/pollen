package gg.moonflower.pollen.core.extension.forge;

import java.util.concurrent.Future;

public interface FMLHandshakeHandlerExtensions {

    void addWait(Future<?> wait);
}
