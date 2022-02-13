package gg.moonflower.pollen.core.mixin.forge;

import gg.moonflower.pollen.api.fluid.PollinatedFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Fluid.class)
public class FluidMixin {

    @Inject(method = "createAttributes", at = @At("HEAD"), remap = false, cancellable = true)
    public void createPollenAttributes(CallbackInfoReturnable<FluidAttributes> cir) {
        if (this instanceof PollinatedFluid) {
            PollinatedFluid pollinatedFluid = (PollinatedFluid) this;
            cir.setReturnValue(FluidAttributes.builder(pollinatedFluid.getStillTextureName(), pollinatedFluid.getFlowingTextureName()).build((Fluid) (Object) this));
        }
    }
}
