package gg.moonflower.pollen.api.registry;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

/**
 * A specialized registry for fluids to handle all the specifics of Minecraft.
 *
 * @author Ocelot
 * @since 1.0.0
 */
public class PollinatedFluidRegistry extends WrapperPollinatedRegistry<Fluid> {

    PollinatedFluidRegistry(PollinatedRegistry<Fluid> fluidRegistry) {
        super(fluidRegistry);
    }

    @Override
    protected void onRegister(Platform mod) {
        super.onRegister(mod);

        this.stream().forEach(fluid -> {
            for (FluidState fluidState : fluid.getStateDefinition().getPossibleStates()) {
                Fluid.FLUID_STATE_REGISTRY.add(fluidState);
            }
        });
    }
}
