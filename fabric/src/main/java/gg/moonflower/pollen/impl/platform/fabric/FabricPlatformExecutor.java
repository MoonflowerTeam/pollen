package gg.moonflower.pollen.impl.platform.fabric;

import net.minecraft.util.thread.BlockableEventLoop;

public interface FabricPlatformExecutor {

    BlockableEventLoop<?> getGameExecutor();
}
