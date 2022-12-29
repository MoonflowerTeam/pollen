package gg.moonflower.pollen.api.registry.client.fabric;

import gg.moonflower.pollen.api.registry.client.ModelRegistry;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ModelRegistryImpl {
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
