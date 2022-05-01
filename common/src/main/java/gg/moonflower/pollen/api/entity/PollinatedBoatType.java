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
    private final Supplier<ItemLike> item;

    public PollinatedBoatType(ResourceLocation texture) {
        this(texture, () -> null);
    }

    public PollinatedBoatType(ResourceLocation texture, ItemLike item) {
        this(texture, () -> item);
    }

    public PollinatedBoatType(ResourceLocation texture, Supplier<ItemLike> item) {
        this.texture = texture;
        this.item = item;
    }

    /**
     * @return The texture of this boat type
     */
    public ResourceLocation getTexture() {
        return texture;
    }

    /**
     * @return The item to give the player when picking or when the boat is broken
     */
    public Item getItem() {
        return this.item.get().asItem();
    }
}
