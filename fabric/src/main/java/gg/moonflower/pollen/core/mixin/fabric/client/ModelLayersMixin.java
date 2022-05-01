package gg.moonflower.pollen.core.mixin.fabric.client;

import gg.moonflower.pollen.api.registry.content.fabric.SignRegistryImpl;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ModelLayers.class)
public class ModelLayersMixin {

    @Inject(method = "createSignModelName", at = @At("HEAD"), cancellable = true)
    private static void createSignModelName(WoodType woodType, CallbackInfoReturnable<ModelLayerLocation> cir) {
        if (woodType instanceof SignRegistryImpl.WoodTypeImpl) {
            ResourceLocation location = ((SignRegistryImpl.WoodTypeImpl) woodType).getId();
            cir.setReturnValue(new ModelLayerLocation(new ResourceLocation(location.getNamespace(), "sign/" + location.getPath()), "main"));
        }
    }
}
