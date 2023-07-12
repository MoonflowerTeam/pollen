package gg.moonflower.pollen.impl.registry.render;

import dev.architectury.injectables.annotations.ExpectPlatform;
import gg.moonflower.pollen.api.registry.render.v1.ModelRegistry;
import gg.moonflower.pollen.core.Pollen;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public class ModelRegistryImpl {

    @ExpectPlatform
    public static void registerSpecial(ResourceLocation location) {
        Pollen.expect();
    }

    @ExpectPlatform
    public static void registerFactory(ModelRegistry.ModelFactory factory) {
        Pollen.expect();
    }

    @ExpectPlatform
    public static BakedModel getModel(ResourceLocation location) {
        return Pollen.expect();
    }
}
