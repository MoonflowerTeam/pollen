package gg.moonflower.pollen.core.mixin.client;

import gg.moonflower.pollen.api.event.events.client.resource.ModelEvents;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin {

    @Inject(method = "loadBlockModel", at = @At("RETURN"))
    public void loadBlockModel(ResourceLocation location, CallbackInfoReturnable<BlockModel> cir) {
        ModelEvents.LOAD_BLOCK_MODEL.invoker().load(location, cir.getReturnValue());
    }
}
