package gg.moonflower.pollen.impl.registry.render.forge;

import gg.moonflower.pollen.api.registry.render.v1.ModelRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ModelRegistryImplImpl {

    private static final Set<ResourceLocation> SPECIAL_MODELS = ConcurrentHashMap.newKeySet();
    private static final Set<ModelRegistry.ModelFactory> FACTORIES = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void onEvent(ModelEvent.RegisterAdditional event) {
        SPECIAL_MODELS.forEach(event::register);
        FACTORIES.forEach(factory -> factory.registerModels(Minecraft.getInstance().getResourceManager(), event::register));
    }

    public static void registerSpecial(ResourceLocation location) {
        SPECIAL_MODELS.add(location);
    }

    public static void registerFactory(ModelRegistry.ModelFactory factory) {
        FACTORIES.add(factory);
    }

    public static BakedModel getModel(ResourceLocation location) {
        return Minecraft.getInstance().getModelManager().getModel(location);
    }
}
