package gg.moonflower.pollen.impl.registry.content;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import gg.moonflower.pollen.api.registry.v1.content.FlammabilityRegistry;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class FlammabilityRegistryImpl {

    @ExpectPlatform
    public static void register(Block fireBlock, Block block, int encouragement, int flammability) {
        Platform.error();
    }
}
