package gg.moonflower.pollen.api.registry.client.forge;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApiStatus.Internal
public class ModelRegistryImpl {

    private static final Set<ResourceLocation> SPECIAL_MODELS = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void onEvent(ModelRegistryEvent event) {
        SPECIAL_MODELS.forEach(ModelLoader::addSpecialModel);
    }

    public static void registerSpecial(ResourceLocation location) {
        SPECIAL_MODELS.add(location);
    }
}
