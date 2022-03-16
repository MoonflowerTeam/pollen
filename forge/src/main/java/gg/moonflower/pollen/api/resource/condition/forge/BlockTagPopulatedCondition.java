package gg.moonflower.pollen.api.resource.condition.forge;

import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class BlockTagPopulatedCondition extends TagPopulatedCondition<Block> {

    public static final ResourceLocation NAME = new ResourceLocation(Pollen.MOD_ID, "block_tag_populated");

    public BlockTagPopulatedCondition(ResourceLocation tag) {
        super(Registry.BLOCK_REGISTRY, tag);
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }
}
