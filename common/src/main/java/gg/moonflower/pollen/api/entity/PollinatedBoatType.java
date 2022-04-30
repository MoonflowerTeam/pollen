package gg.moonflower.pollen.api.entity;

import net.minecraft.resources.ResourceLocation;

/***
 * @author Ocelot
 * @since 1.4.0
 */
public class PollinatedBoatType {

    private final ResourceLocation texture;

    public PollinatedBoatType(ResourceLocation texture) {
        this.texture = texture;
    }

    /**
     * @return The texture of this boat type
     */
    public ResourceLocation getTexture() {
        return texture;
    }
}
