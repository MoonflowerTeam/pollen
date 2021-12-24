package gg.moonflower.pollen.api.registry.client;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public final class RenderTypeRegistry {

    private RenderTypeRegistry() {
    }

    @ExpectPlatform
    public static void register(Block block, RenderType type) {
        Platform.error();
    }

    @ExpectPlatform
    public static void register(Fluid fluid, RenderType type) {
        Platform.error();
    }
}
