package gg.moonflower.pollen.api.registry.v1.render;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

/**
 * Registers render layers for blocks and items.
 */
public interface RenderTypeRegistry {

    @ExpectPlatform
    static void register(Block block, RenderType type) {
        Platform.error();
    }

    @ExpectPlatform
    static void register(Fluid fluid, RenderType type) {
        Platform.error();
    }
}
