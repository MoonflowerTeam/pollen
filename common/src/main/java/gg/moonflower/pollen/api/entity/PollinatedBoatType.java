package gg.moonflower.pollen.api.entity;

import gg.moonflower.pollen.api.item.PollinatedBoatItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

/**
 * A custom type of boat that can be summoned as a type of {@link PollinatedBoat}.
 *
 * @author Ocelot
 * @see PollinatedBoat
 * @see PollinatedBoatItem
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
