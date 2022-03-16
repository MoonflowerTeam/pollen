package gg.moonflower.pollen.api.resource.condition.forge;

import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class ItemTagPopulatedCondition extends TagPopulatedCondition<Item> {

    public static final ResourceLocation NAME = new ResourceLocation(Pollen.MOD_ID, "item_tag_populated");

    public ItemTagPopulatedCondition(ResourceLocation tag) {
        super(Registry.ITEM_REGISTRY, tag);
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }
}
