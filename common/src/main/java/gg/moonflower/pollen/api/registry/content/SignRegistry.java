package gg.moonflower.pollen.api.registry.content;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;

/**
 * @author Jackson
 * @since 1.4.0
 */
public final class SignRegistry {

    private SignRegistry() {
    }

    /**
     * Registers a sign type into {@link WoodType} and adds it to the atlas.
     * <p>Should not be used unless needed. Use {@link gg.moonflower.pollen.api.registry.PollinatedBlockRegistry#registerSign} instead.
     *
     * @param id The id of the sign
     * @return The registered wood type
     */
    @ExpectPlatform
    public static WoodType register(ResourceLocation id) {
        return Platform.error();
    }
}
