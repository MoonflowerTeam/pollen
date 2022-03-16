package gg.moonflower.pollen.api.resource.condition.forge;

import gg.moonflower.pollen.core.Pollen;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author Ocelot
 */
@ApiStatus.Internal
public class FluidTagPopulatedCondition extends TagPopulatedCondition<Fluid> {

    public static final ResourceLocation NAME = new ResourceLocation(Pollen.MOD_ID, "fluid_tag_populated");

    public FluidTagPopulatedCondition(ResourceLocation tag) {
        super(Registry.FLUID_REGISTRY, tag);
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }
}
