package gg.moonflower.pollen.api.registry.v1.content;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.base.platform.Platform;
import gg.moonflower.pollen.api.registry.v1.PollinatedBlockRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * @author Jackson
 * @since 1.4.0
 */
public interface SignRegistry {

    /**
     * Registers a sign type into {@link WoodType} and adds it to the atlas.
     * <p>Should not be used unless needed. Use {@link PollinatedBlockRegistry#registerSign} instead.
     *
     * @param id The id of the sign
     * @return The registered wood type
     */
    @ExpectPlatform
    static WoodType register(ResourceLocation id) {
        return Platform.error();
    }
}
