package gg.moonflower.pollen.core.mixin.client;

import com.google.common.collect.ImmutableMap;
import gg.moonflower.pollen.api.PollenRegistries;
import gg.moonflower.pollen.core.client.render.entity.PollinatedBoatRenderer;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Map;

@Mixin(LayerDefinitions.class)
public class LayerDefinitionsMixin {

    @Inject(method = "createRoots", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/blockentity/SignRenderer;createSignLayer()Lnet/minecraft/client/model/geom/builders/LayerDefinition;"), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private static void createRoots(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir, ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder) {
        PollenRegistries.BOAT_TYPE_REGISTRY.stream().forEach(type -> {
            builder.put(PollinatedBoatRenderer.createBoatModelName(type), BoatModel.createBodyModel(false));
            builder.put(PollinatedBoatRenderer.createChestBoatModelName(type), BoatModel.createBodyModel(true));
        });
    }
}
