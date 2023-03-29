package gg.moonflower.pollen.impl.registry.render.fabric;

import gg.moonflower.pollen.api.registry.render.v1.ModelRegistry;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;

public class ModelRegistryImplImpl {
    public static void registerSpecial(ResourceLocation location) {
        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(location));
    }

    public static void registerFactory(ModelRegistry.ModelFactory factory) {
        ModelLoadingRegistry.INSTANCE.registerModelProvider(factory::registerModels);
    }

    public static BakedModel getModel(ResourceLocation location) {
        return BakedModelManagerHelper.getModel(Minecraft.getInstance().getModelManager(), location);
    }
}
