package gg.moonflower.pollen.core.mixin.fabric;

import gg.moonflower.pollen.api.platform.Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Fabric doesn't properly register tags, so they need to be tested against connection tags
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    @Nullable
    protected Tag<Fluid> fluidOnEyes;

    @Shadow
    public Level level;

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getEyeY();

    @Shadow
    public abstract double getZ();

    @Inject(method = "updateFluidOnEyes", at = @At("TAIL"))
    public void updateFluidOnEyes(CallbackInfo ci) {
        double d = this.getEyeY() - 0.11111111F;
        BlockPos blockPos = new BlockPos(this.getX(), d, this.getZ());
        FluidState fluidState = this.level.getFluidState(blockPos);

        Platform.getTags().ifPresent(tags -> {
            TagCollection<Fluid> fluidTags = tags.getOrEmpty(Registry.FLUID_REGISTRY);
            for (Tag<Fluid> tag : fluidTags.getAllTags().values()) {
                if ((!(tag instanceof Tag.Named) || !FluidTags.getStaticTags().contains(tag)) && fluidState.is(tag)) {
                    double e = (float) blockPos.getY() + fluidState.getHeight(this.level, blockPos);
                    if (e > d) {
                        this.fluidOnEyes = tag;
                    }

                    return;
                }
            }
        });
    }

    @Inject(method = "isEyeInFluid", at = @At("HEAD"), cancellable = true)
    public void isEyeInFluid(Tag<Fluid> tag, CallbackInfoReturnable<Boolean> cir) {
        if (this.fluidOnEyes == null || tag instanceof Tag.Named && FluidTags.getStaticTags().contains(tag))
            return;
        cir.setReturnValue(tag.getValues().size() == this.fluidOnEyes.getValues().size() && tag.getValues().containsAll(this.fluidOnEyes.getValues()));
    }
}
