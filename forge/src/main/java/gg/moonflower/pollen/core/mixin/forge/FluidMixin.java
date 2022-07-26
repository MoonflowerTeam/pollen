package gg.moonflower.pollen.core.mixin.forge;

import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Fluid.class)
public class FluidMixin {

    // TODO: redo fluid api
//    @Inject(method = "createAttributes", at = @At("HEAD"), remap = false, cancellable = true)
//    public void createPollenAttributes(CallbackInfoReturnable<FluidAttributes> cir) {
//        if (this instanceof PollinatedFluid) {
//            PollinatedFluid pollinatedFluid = (PollinatedFluid) this;
//            cir.setReturnValue(FluidAttributes.builder(pollinatedFluid.getStillTextureName(), pollinatedFluid.getFlowingTextureName()).build((Fluid) (Object) this));
//        }
//    }
}
