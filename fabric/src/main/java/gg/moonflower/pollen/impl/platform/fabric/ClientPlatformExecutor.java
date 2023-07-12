package gg.moonflower.pollen.impl.platform.fabric;

import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.BlockableEventLoop;

public class ClientPlatformExecutor implements FabricPlatformExecutor {

    @Override
    public BlockableEventLoop<?> getGameExecutor() {
        return Minecraft.getInstance();
    }
}
