package gg.moonflower.pollen.impl.platform.fabric;

import gg.moonflower.pollen.api.platform.v1.Platform;
import net.minecraft.util.thread.BlockableEventLoop;

public class DefaultPlatformExecutor implements FabricPlatformExecutor {

    @Override
    public BlockableEventLoop<?> getGameExecutor() {
        return Platform.getRunningServer().orElseThrow(() -> new IllegalStateException("Expected server to be running"));
    }
}
